package com.karan.craftingtable.services;

import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.RespondToInviteRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import com.karan.craftingtable.models.responses.RespondToInviteResponseDTO;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectMemberResponseDTO> getProjectMembers(Long projectId);

    ProjectMemberResponseDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO);

    ProjectMemberResponseDTO updateProjectMemberRole(Long projectId, UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO);

    Void removeProjectMember(Long projectId, Long projectMemberId);

    RespondToInviteResponseDTO respondToInvite(Long projectId, RespondToInviteRequestDTO respondToInviteRequestDTO);

}
