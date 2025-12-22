package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import com.karan.craftingtable.services.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImplementation implements ProjectMemberService {

    @Override
    public List<ProjectMemberResponseDTO> getProjectMembers(long projectId) {
        return List.of();
    }

    @Override
    public ProjectMemberResponseDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        return null;
    }

    @Override
    public ProjectMemberResponseDTO updateProjectMemberRole(Long projectId, Long projectMemberId, UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO) {
        return null;
    }

    @Override
    public Void deleteProjectMember(Long projectId, Long projectMemberId) {
        return null;
    }

}
