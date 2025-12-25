package com.karan.craftingtable.models.wrappers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FieldValidationError {

    private final String field;
    private final Object rejectedValue;
    private final String message;

}
