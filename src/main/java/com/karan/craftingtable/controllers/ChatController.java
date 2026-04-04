package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.ChatRequestDTO;
import com.karan.craftingtable.models.responses.ChatResponseDTO;
import com.karan.craftingtable.models.responses.StreamResponseDTO;
import com.karan.craftingtable.models.wrappers.APIResponse;
import com.karan.craftingtable.services.AIService;
import com.karan.craftingtable.services.ChatService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final AIService aiService;
    private final ChatClient chatClient;
    private final ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamResponseDTO>> streamResponses(
            @RequestBody ChatRequestDTO chatRequest
    ) {
        return aiService.streamResponses(chatRequest.userPrompt(), chatRequest.projectId())
                .map(data -> ServerSentEvent.<StreamResponseDTO>builder()
                        .data(data)
                        .build()
                );
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<APIResponse<List<ChatResponseDTO>>> getChatHistory(
            @PathVariable Long projectId
    ) {
        List<ChatResponseDTO> response = chatService.getProjectChatHistory(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    // only for testing purposes
    @PostMapping("/chat")
    Output chat(@RequestBody Input input) {
        String response = chatClient.prompt(input.prompt()).call().content();
        return new Output(response);
    }

    record Input(@NotBlank String prompt) {}
    record Output(String content) {}

}
