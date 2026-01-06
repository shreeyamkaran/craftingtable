package com.karan.craftingtable.models.requests;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectMemberRoleRequestDTO(
        @NotNull Long projectMemberId,
        @NotNull ProjectMemberRoleEnum projectMemberRole
) { }
