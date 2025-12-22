package com.karan.craftingtable.models.requests;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;

public record UpdateProjectMemberRoleRequestDTO(
        ProjectMemberRoleEnum projectMemberRole
) { }
