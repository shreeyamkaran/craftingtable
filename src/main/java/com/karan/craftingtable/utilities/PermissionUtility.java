package com.karan.craftingtable.utilities;

import com.karan.craftingtable.enums.PermissionEnum;
import com.karan.craftingtable.repositories.ProjectMemberRepository;
import com.karan.craftingtable.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("permissionUtility")
@RequiredArgsConstructor
public class PermissionUtility {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthService authService;

    public boolean canViewProject(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.VIEW_PROJECT);
    }

    public boolean canEditProject(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.EDIT_PROJECT);
    }

    public boolean canDeleteProject(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.DELETE_PROJECT);
    }

    public boolean canViewProjectMembers(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.VIEW_PROJECT_MEMBERS);
    }

    public boolean canInviteProjectMembers(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.INVITE_PROJECT_MEMBERS);
    }

    public boolean canUpdateProjectMembers(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.UPDATE_PROJECT_MEMBERS);
    }

    public boolean canRemoveProjectMembers(Long projectId) {
        return hasPermissionOnProject(projectId, PermissionEnum.REMOVE_PROJECT_MEMBERS);
    }

    private boolean hasPermissionOnProject(Long projectId, PermissionEnum permission) {
        Long userId = authService.getCurrentLoggedInUser().getId();
        return projectMemberRepository.findProjectMemberRoleByIdProjectIdAndIdProjectMemberId(projectId, userId)
                .map(role -> role.getPermissions().contains(permission))
                .orElse(false);
    }

}
