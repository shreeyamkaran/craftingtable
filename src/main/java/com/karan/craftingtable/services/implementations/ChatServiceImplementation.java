package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ChatMessageEntity;
import com.karan.craftingtable.entities.ChatSessionEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.mappers.ChatMapper;
import com.karan.craftingtable.models.responses.ChatResponseDTO;
import com.karan.craftingtable.repositories.ChatMessageRepository;
import com.karan.craftingtable.repositories.ChatSessionRepository;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImplementation implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthService authService;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponseDTO> getProjectChatHistory(Long projectId) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        Long userId = currentLoggedInUser.getId();
        ChatSessionEntity chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionEntity.ChatSessionEntityId(projectId, userId)
        );
        List<ChatMessageEntity> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }

}
