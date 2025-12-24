package com.karan.craftingtable.models.requests;

public record RespondToInviteRequestDTO(
        Long inviteeId,
        Long projectId,
        Boolean wantToAcceptTheInvite
) { }
