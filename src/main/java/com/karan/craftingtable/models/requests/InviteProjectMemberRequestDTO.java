package com.karan.craftingtable.models.requests;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteProjectMemberRequestDTO(
        @Email @NotBlank String email,
        @NotNull ProjectMemberRoleEnum projectMemberRole
) { }
