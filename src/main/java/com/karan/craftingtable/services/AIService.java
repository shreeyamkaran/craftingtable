package com.karan.craftingtable.services;

import reactor.core.publisher.Flux;

public interface AIService {

    Flux<String> streamResponses(String userPrompt, Long projectId);

}
