package com.karan.craftingtable.services;

import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectMemberResponseDTO> getProjectMembers(Long projectId);

    ProjectMemberResponseDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO);

    ProjectMemberResponseDTO updateProjectMemberRole(Long projectId, Long projectMemberId, UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO);

    Void deleteProjectMember(Long projectId, Long projectMemberId);

}
