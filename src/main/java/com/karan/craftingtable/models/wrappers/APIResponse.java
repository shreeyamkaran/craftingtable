package com.karan.craftingtable.models.wrappers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class APIResponse<T> {

    private final boolean success;
    private final T data;
    private final APIError error;
    private final Object meta;

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(true, data, null, null);
    }

    public static <T> APIResponse<T> success(T data, Object meta) {
        return new APIResponse<>(true, data, null, meta);
    }

    public static <T> APIResponse<T> failure(APIError error) {
        return new APIResponse<>(false, null, error, null);
    }

}
