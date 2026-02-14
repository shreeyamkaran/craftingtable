package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.llms.advisors.FileTreeContextAdvisor;
import com.karan.craftingtable.llms.tools.CodeGenerationTool;
import com.karan.craftingtable.services.AIService;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.ProjectFileService;
import com.karan.craftingtable.llms.PromptUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImplementation implements AIService {

    private final ChatClient chatClient;
    private final AuthService authService;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@permissionUtility.canEditProject(#projectId)")
    public Flux<String> streamResponses(String userPrompt, Long projectId) {
        UserEntity currentLoggedUser = authService.getCurrentLoggedInUser();
        Long userId = currentLoggedUser.getId();
        // Create a chat session if it does not exist already
        this.createNewChatSession(userId, projectId);
        Map<String,Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );
        StringBuilder completeResponseBuffer = new StringBuilder();
        CodeGenerationTool codeGenerationTool = new CodeGenerationTool(projectFileService, projectId);
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
                    completeResponseBuffer.append(content);
                })
                .doOnComplete(() -> Schedulers.boundedElastic().schedule(() -> parseAndSaveFiles(String.valueOf(completeResponseBuffer), projectId)))
                .doOnError(error -> log.error("Error occurred during streaming :: {}", error.getMessage()))
                .map(chatResponse -> Objects.requireNonNull(chatResponse.getResult().getOutput().getText()));
    }

    private void createNewChatSession(Long userId, Long projectId) {
    }

    private void parseAndSaveFiles(String completeResponse, Long projectId) {
        Matcher matcher = FILE_TAG_PATTERN.matcher(completeResponse);
        while (matcher.find()) {
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();
            projectFileService.saveFile(projectId, filePath, fileContent);
        }
    }

}
