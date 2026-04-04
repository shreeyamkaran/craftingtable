package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ChatEventEntity;
import com.karan.craftingtable.entities.ChatMessageEntity;
import com.karan.craftingtable.entities.ChatSessionEntity;
import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.enums.ChatEventTypeEnum;
import com.karan.craftingtable.enums.MessageSenderRoleEnum;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.llms.LLMResponseParser;
import com.karan.craftingtable.llms.PromptUtility;
import com.karan.craftingtable.llms.advisors.FileTreeContextAdvisor;
import com.karan.craftingtable.llms.tools.CodeGenerationTool;
import com.karan.craftingtable.models.responses.StreamResponseDTO;
import com.karan.craftingtable.repositories.ChatEventRepository;
import com.karan.craftingtable.repositories.ChatMessageRepository;
import com.karan.craftingtable.repositories.ChatSessionRepository;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.AIService;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.ProjectFileService;
import com.karan.craftingtable.services.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImplementation implements AIService {

    private final ChatClient chatClient;
    private final AuthService authService;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LLMResponseParser llmResponseParser;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UsageService usageService;

    @Override
    @PreAuthorize("@permissionUtility.canEditProject(#projectId)")
    public Flux<StreamResponseDTO> streamResponses(String userPrompt, Long projectId) {
        UserEntity currentLoggedUser = authService.getCurrentLoggedInUser();
        Long userId = currentLoggedUser.getId();
        // Create a chat session if it does not exist already
        ChatSessionEntity chatSession =  this.createNewChatSession(userId, projectId);
        Map<String,Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );
        StringBuilder completeResponseBuffer = new StringBuilder();
        CodeGenerationTool codeGenerationTool = new CodeGenerationTool(projectFileService, projectId);
        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();
        return chatClient
                .prompt()
                .system(PromptUtility.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userPrompt)
                .tools(codeGenerationTool)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    String content = chatResponse.getResult().getOutput().getText();
                    if(content != null && !content.isEmpty() && endTime.get() == 0) { // first non-empty chunk received
                        endTime.set(System.currentTimeMillis());
                    }
                    if(chatResponse.getMetadata().getUsage() != null) {
                        usageRef.set(chatResponse.getMetadata().getUsage());
                    }
                    completeResponseBuffer.append(content);
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(completeResponseBuffer.toString(), projectId);
                        long duration = (endTime.get() - startTime.get()) /  1000;
                        finalizeChats(userPrompt, chatSession, completeResponseBuffer.toString(), duration, usageRef.get());
                    });
                })
                .doOnError(error -> log.error("Error occurred during streaming :: {}", error.getMessage()))
                .map(chatResponse -> {
                            String text = chatResponse.getResult().getOutput().getText();
                            return new StreamResponseDTO(text != null ? text : "");
                });
    }

    private void finalizeChats(String userMessage, ChatSessionEntity chatSession, String fullText, Long duration, Usage usage) {
        Long projectId = chatSession.getProject().getId();

        if(usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getUser().getId(), totalTokens);
        }

        // Save the User message
        chatMessageRepository.save(
                ChatMessageEntity.builder()
                        .chatSession(chatSession)
                        .messageSenderRole(MessageSenderRoleEnum.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessageEntity assistantChatMessage = ChatMessageEntity.builder()
                .messageSenderRole(MessageSenderRoleEnum.ASSISTANT)
                .content("Assistant message here...")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEventEntity> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEventEntity.builder()
                .chatEventType(ChatEventTypeEnum.THOUGHT)
                .chatMessage(assistantChatMessage)
                .content("Thought for " + duration + "s")
                .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(e -> e.getChatEventType() == ChatEventTypeEnum.FILE_EDIT)
                .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }

    private ChatSessionEntity createNewChatSession(Long userId, Long projectId) {
        ChatSessionEntity.ChatSessionEntityId chatSessionId = new ChatSessionEntity.ChatSessionEntityId(projectId, userId);
        ChatSessionEntity chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession == null) {
            ProjectEntity project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project with id " + projectId + " not found"));
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
            chatSession = ChatSessionEntity.builder()
                    .chatSessionEntityId(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }

}
