package com.karan.craftingtable.models.responses;

public record FileNodeResponseDTO(
        String path
) {

    @Override
    public String toString() {
        return path;
    }

}
