package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.ChatResponseDTO;

import java.util.List;

public interface ChatService {

    List<ChatResponseDTO> getProjectChatHistory(Long projectId);

}
