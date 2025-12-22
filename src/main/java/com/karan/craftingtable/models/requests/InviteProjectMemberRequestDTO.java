package com.karan.craftingtable.models.requests;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;

public record InviteProjectMemberRequestDTO(
        String email,
        ProjectMemberRoleEnum projectMemberRole
) { }
