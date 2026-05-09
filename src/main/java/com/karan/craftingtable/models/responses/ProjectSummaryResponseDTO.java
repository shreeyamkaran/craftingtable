package com.karan.craftingtable.models.responses;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;

import java.time.Instant;

public record ProjectSummaryResponseDTO(
        Long id,
        String name,
        Instant createdAt,
        Instant lastModifiedAt,
        ProjectMemberRoleEnum role
) { }
