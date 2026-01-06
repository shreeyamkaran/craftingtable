package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.NotNull;

public record RespondToInviteRequestDTO(
        @NotNull Boolean wantToAcceptTheInvite
) { }
