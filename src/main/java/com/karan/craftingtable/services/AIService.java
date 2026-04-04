package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.StreamResponseDTO;
import reactor.core.publisher.Flux;

public interface AIService {

    Flux<StreamResponseDTO> streamResponses(String userPrompt, Long projectId);

}
