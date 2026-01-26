package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.ChatRequestDTO;
import com.karan.craftingtable.services.AIService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final AIService aiService;
    private final ChatClient chatClient;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamResponses(
            @RequestBody ChatRequestDTO chatRequest
    ) {
        return aiService.streamResponses(chatRequest.userPrompt(), chatRequest.projectId())
                .map(data -> ServerSentEvent.<String>builder()
                        .data(data)
                        .build()
                );
    }

    @PostMapping("/chat")
    Output chat(@RequestBody Input input) {
        String response = chatClient.prompt(input.prompt()).call().content();
        return new Output(response);
    }

    record Input(@NotBlank String prompt) {}
    record Output(String content) {}

}
