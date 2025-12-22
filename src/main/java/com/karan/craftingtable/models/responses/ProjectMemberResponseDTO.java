package com.karan.craftingtable.models.responses;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;

import java.time.Instant;

public record ProjectMemberResponseDTO(
        Long id,
        String name,
        String email,
        String avatarURL,
        ProjectMemberRoleEnum projectMemberRole,
        Instant invitedAt
) { }
