package com.karan.craftingtable.models.wrappers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class APIError {

    private final String code;
    private final String message;
    private final String path;
    private final int status;
    private final Instant timestamp = Instant.now();
    private final List<FieldValidationError> fieldErrors;

}
