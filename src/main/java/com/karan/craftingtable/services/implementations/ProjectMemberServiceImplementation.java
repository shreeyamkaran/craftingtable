package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.ProjectMemberEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
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
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberServiceImplementation implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    @PreAuthorize("@permissionUtility.canViewProjectMembers(#projectId)")
    public List<ProjectMemberResponseDTO> getProjectMembers(Long projectId) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        List<ProjectMemberEntity> projectMemberEntityList = projectMemberRepository.findByIdProjectIdAndInviteAcceptedAtIsNotNull(projectEntity.getId());
        return projectMemberMapper.toProjectMemberResponseDTOListFromProjectMemberEntityList(projectMemberEntityList);
    }

    @Override
    @PreAuthorize("@permissionUtility.canInviteProjectMembers(#projectId)")
    public ProjectMemberResponseDTO inviteProjectMember(Long projectId, InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
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
        projectMemberRepository.findById(projectMemberEntityId).ifPresent(entity -> {
            if (entity.getInviteAcceptedAt() != null) {
                throw new BadRequestException("This project member already exists");
            } else {
                throw new BadRequestException("Invitation already exists");
            }
        });
        ProjectMemberEntity newlyInvitedProjectMember =
                ProjectMemberEntity.builder()
                        .id(projectMemberEntityId)
                        .project(projectEntity)
                        .projectMember(invitee)
                        .projectMemberRole(ProjectMemberRoleEnum.VIEWER)
                        .invitedAt(Instant.now())
                        .build();
        projectMemberRepository.save(newlyInvitedProjectMember);
        return projectMemberMapper.toProjectMemberResponseDTOFromProjectMemberEntity(newlyInvitedProjectMember);
    }

    @Override
    @PreAuthorize("@permissionUtility.canUpdateProjectMembers(#projectId)")
    public ProjectMemberResponseDTO updateProjectMemberRole(Long projectId, UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        Long projectMemberId = updateProjectMemberRoleRequestDTO.projectMemberId();
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
    @PreAuthorize("@permissionUtility.canRemoveProjectMembers(#projectId)")
    public Void removeProjectMember(Long projectId, Long projectMemberId) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        if (Objects.equals(projectMemberId, currentLoggedInUser.getId())) {
            throw new UnauthorizedException("You are not allowed to remove yourself");
        }
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
    public RespondToInviteResponseDTO respondToInvite(Long projectId, RespondToInviteRequestDTO respondToInviteRequestDTO) {
        UserEntity currentLoggedInUser = authService.getCurrentLoggedInUser();
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId
                = new ProjectMemberEntity.ProjectMemberEntityId(projectId, currentLoggedInUser.getId());
        ProjectMemberEntity projectMemberEntity =
                projectMemberRepository.findById(projectMemberEntityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Project invitation not found"));
        if (projectMemberEntity.getProjectMemberRole() == ProjectMemberRoleEnum.OWNER) {
            throw new BadRequestException("You are the owner of this project");
        }
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
