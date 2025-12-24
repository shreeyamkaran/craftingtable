package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.ProjectMemberEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.mappers.ProjectMemberMapper;
import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import com.karan.craftingtable.repositories.ProjectMemberRepository;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.ProjectMemberService;
import com.karan.craftingtable.utilities.LoggedInUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberServiceImplementation implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final LoggedInUserProvider loggedInUserProvider;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;

    @Override
    public List<ProjectMemberResponseDTO> getProjectMembers(Long projectId) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity = projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId()).orElseThrow();
        List<ProjectMemberResponseDTO> projectOwnerAndMemberResponseDTOList = new ArrayList<>();
        // first add the project owner
        UserEntity projectOwner = projectEntity.getProjectOwner();
        ProjectMemberResponseDTO projectOwnerResponseDTO = projectMemberMapper.toProjectMemberResponseDTOFromProjectOwnerUserEntity(projectOwner);
        projectOwnerAndMemberResponseDTOList.add(projectOwnerResponseDTO);
        // then add the project members
        List<ProjectMemberEntity> projectMemberEntityList = projectMemberRepository.findByIdProjectId(projectEntity.getId());
        List<ProjectMemberResponseDTO> projectMemberResponseDTOList = projectMemberMapper.toProjectMemberResponseDTOListFromProjectMemberEntityList(projectMemberEntityList);
        projectOwnerAndMemberResponseDTOList.addAll(projectMemberResponseDTOList);
        return projectOwnerAndMemberResponseDTOList;
    }

    @Override
    public ProjectMemberResponseDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity = projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId()).orElseThrow();
        if (!projectEntity.getProjectOwner().getId().equals(currentLoggedInUser.getId())) {
            throw new RuntimeException("You are not allowed to invite members in this project");
        }
        if (inviteProjectMemberRequestDTO.email().equals(currentLoggedInUser.getEmail())) {
            throw new RuntimeException("You are not allowed to invite yourself");
        }
        UserEntity invitee = userRepository.findByEmail(inviteProjectMemberRequestDTO.email()).orElseThrow();
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, invitee.getId());
        if (projectMemberRepository.existsById(projectMemberEntityId)) {
            throw new RuntimeException("This project member already exists");
        }
        ProjectMemberEntity newlyInvitedProjectMember =
                ProjectMemberEntity.builder()
                        .id(projectMemberEntityId)
                        .project(projectEntity)
                        .projectMember(invitee)
                        .projectMemberRole(inviteProjectMemberRequestDTO.projectMemberRole())
                        .invitedAt(Instant.now())
                        .inviteAcceptedAt(Instant.now()) // TODO: make another API to accept invitations
                        .build();
        projectMemberRepository.save(newlyInvitedProjectMember);
        return projectMemberMapper.toProjectMemberResponseDTOFromProjectMemberEntity(newlyInvitedProjectMember);
    }

    @Override
    public ProjectMemberResponseDTO updateProjectMemberRole(Long projectId, Long projectMemberId, UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity = projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId()).orElseThrow();
        if (!projectEntity.getProjectOwner().getId().equals(currentLoggedInUser.getId())) {
            throw new RuntimeException("You are not allowed to update project members' roles in this project");
        }
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, projectMemberId);
        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findById(projectMemberEntityId).orElseThrow();
        projectMemberEntity.setProjectMemberRole(updateProjectMemberRoleRequestDTO.projectMemberRole());
        return projectMemberMapper.toProjectMemberResponseDTOFromProjectMemberEntity(projectMemberEntity);
    }

    @Override
    public Void removeProjectMember(Long projectId, Long projectMemberId) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity = projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId()).orElseThrow();
        if (!projectEntity.getProjectOwner().getId().equals(currentLoggedInUser.getId())) {
            throw new RuntimeException("You are not allowed to remove project members from this project");
        }
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, projectMemberId);
        if (!projectMemberRepository.existsById(projectMemberEntityId)) {
            throw new RuntimeException("This project member does not exist");
        }
        projectMemberRepository.deleteById(projectMemberEntityId);
        return null;
    }

}
