package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.ProjectMemberEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.exceptions.BadRequestException;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.exceptions.UnauthorizedException;
import com.karan.craftingtable.mappers.ProjectMemberMapper;
import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.RespondToInviteRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import com.karan.craftingtable.models.responses.RespondToInviteResponseDTO;
import com.karan.craftingtable.repositories.ProjectMemberRepository;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.ProjectMemberService;
import com.karan.craftingtable.utilities.LoggedInUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        List<ProjectMemberEntity> projectMemberEntityList = projectMemberRepository.findByIdProjectIdAndInviteAcceptedAtIsNotNull(projectEntity.getId());
        return projectMemberMapper.toProjectMemberResponseDTOListFromProjectMemberEntityList(projectMemberEntityList);
    }

    @Override
    public ProjectMemberResponseDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        if (inviteProjectMemberRequestDTO.email().equals(currentLoggedInUser.getEmail())) {
            throw new UnauthorizedException("You are not allowed to invite yourself");
        }
        UserEntity invitee =
                userRepository.findByEmail(inviteProjectMemberRequestDTO.email())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + inviteProjectMemberRequestDTO.email()));
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, invitee.getId());
        ProjectMemberEntity projectMemberEntity = projectMemberRepository.findById(projectMemberEntityId).orElse(null);
        if (projectMemberEntity != null && projectMemberEntity.getInviteAcceptedAt() != null) {
            throw new BadRequestException("This project member already exists");
        }
        if (projectMemberEntity != null) {
            throw new BadRequestException("Invitation already exists");
        }
        ProjectMemberEntity newlyInvitedProjectMember =
                ProjectMemberEntity.builder()
                        .id(projectMemberEntityId)
                        .project(projectEntity)
                        .projectMember(invitee)
                        .projectMemberRole(inviteProjectMemberRequestDTO.projectMemberRole())
                        .invitedAt(Instant.now())
                        .build();
        projectMemberRepository.save(newlyInvitedProjectMember);
        return projectMemberMapper.toProjectMemberResponseDTOFromProjectMemberEntity(newlyInvitedProjectMember);
    }

    @Override
    public ProjectMemberResponseDTO updateProjectMemberRole(Long projectId, Long projectMemberId, UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, projectMemberId);
        ProjectMemberEntity projectMemberEntity =
                projectMemberRepository.findById(projectMemberEntityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project member entity not found with projectId " + projectMemberEntityId.getProjectId() + " and projectMemberId " + projectMemberEntityId.getProjectMemberId()));
        projectMemberEntity.setProjectMemberRole(updateProjectMemberRoleRequestDTO.projectMemberRole());
        return projectMemberMapper.toProjectMemberResponseDTOFromProjectMemberEntity(projectMemberEntity);
    }

    @Override
    public Void removeProjectMember(Long projectId, Long projectMemberId) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, projectMemberId);
        if (!projectMemberRepository.existsById(projectMemberEntityId)) {
            throw new ResourceNotFoundException("Project member entity not found with projectId " + projectMemberEntityId.getProjectId() + " and projectMemberId " + projectMemberEntityId.getProjectMemberId());
        }
        projectMemberRepository.deleteById(projectMemberEntityId);
        return null;
    }

    @Override
    public RespondToInviteResponseDTO respondToInvite(RespondToInviteRequestDTO respondToInviteRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        Long inviteeId = respondToInviteRequestDTO.inviteeId();
        Long projectId = respondToInviteRequestDTO.projectId();
        if (!inviteeId.equals(currentLoggedInUser.getId())) {
            throw new UnauthorizedException("You are not invited to this project");
        }
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, inviteeId);
        ProjectMemberEntity projectMemberEntity =
                projectMemberRepository.findById(projectMemberEntityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project member entity not found with projectId " + projectMemberEntityId.getProjectId() + " and projectMemberId " + projectMemberEntityId.getProjectMemberId()));
        if (projectMemberEntity.getInviteAcceptedAt() != null) {
            throw new BadRequestException("Invitation has already been accepted");
        }
        RespondToInviteResponseDTO response = new RespondToInviteResponseDTO(false);
        if (respondToInviteRequestDTO.wantToAcceptTheInvite()) {
            response = new RespondToInviteResponseDTO(true);
            projectMemberEntity.setInviteAcceptedAt(Instant.now());
        } else {
            projectMemberRepository.deleteById(projectMemberEntityId);
        }
        return response;
    }

}
