package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.NotNull;

public record RespondToInviteRequestDTO(
        @NotNull Long inviteeId,
        @NotNull Long projectId,
        @NotNull Boolean wantToAcceptTheInvite
) { }
