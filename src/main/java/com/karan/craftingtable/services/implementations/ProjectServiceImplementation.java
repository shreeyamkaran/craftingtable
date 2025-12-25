package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.ProjectMemberEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.mappers.ProjectMapper;
import com.karan.craftingtable.models.requests.ProjectRequestDTO;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import com.karan.craftingtable.models.responses.ProjectSummaryResponseDTO;
import com.karan.craftingtable.repositories.ProjectMemberRepository;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.services.ProjectService;
import com.karan.craftingtable.utilities.LoggedInUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImplementation implements ProjectService {

    private final ProjectRepository projectRepository;
    private final LoggedInUserProvider loggedInUserProvider;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public List<ProjectSummaryResponseDTO> getAllProjects() {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        List<ProjectEntity> projectEntities = projectRepository.findAllAccessibleProjects(currentLoggedInUser.getId());
        return projectMapper.toProjectSummaryResponseDTOList(projectEntities);
    }

    @Override
    public ProjectResponseDTO getProjectById(Long projectId) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        return projectMapper.toProjectResponseDTO(projectEntity);
    }

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name(projectRequestDTO.name())
                .build();
        projectEntity = projectRepository.save(projectEntity);
        ProjectMemberEntity.ProjectMemberEntityId projectMemberEntityId =
                new ProjectMemberEntity.ProjectMemberEntityId(projectEntity.getId(), currentLoggedInUser.getId());
        ProjectMemberEntity projectMemberEntity = ProjectMemberEntity.builder()
                .id(projectMemberEntityId)
                .project(projectEntity)
                .projectMember(currentLoggedInUser)
                .projectMemberRole(ProjectMemberRoleEnum.OWNER)
                .invitedAt(Instant.now())
                .inviteAcceptedAt(Instant.now())
                .build();
        projectMemberRepository.save(projectMemberEntity);
        return projectMapper.toProjectResponseDTO(projectEntity);
    }

    @Override
    public ProjectResponseDTO updateProjectById(Long projectId, ProjectRequestDTO projectRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        projectEntity.setName(projectRequestDTO.name());
        return projectMapper.toProjectResponseDTO(projectEntity);
    }

    @Override
    public Void softDeleteProjectById(Long projectId) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity =
                projectRepository.findAccessibleProjectById(projectId, currentLoggedInUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));
        projectEntity.setDeletedAt(Instant.now());
        return null;
    }

}
