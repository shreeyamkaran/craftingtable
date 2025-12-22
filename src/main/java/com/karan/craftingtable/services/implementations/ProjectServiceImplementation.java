package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.mappers.ProjectMapper;
import com.karan.craftingtable.models.requests.ProjectRequestDTO;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import com.karan.craftingtable.models.responses.ProjectSummaryResponseDTO;
import com.karan.craftingtable.repositories.ProjectRepository;
import com.karan.craftingtable.services.ProjectService;
import com.karan.craftingtable.utilities.LoggedInUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImplementation implements ProjectService {

    private final ProjectRepository projectRepository;
    private final LoggedInUserProvider loggedInUserProvider;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponseDTO> getAllProjects() {
        return List.of();
    }

    @Override
    public ProjectResponseDTO getProjectById(Long projectId) {
        return null;
    }

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO) {
        UserEntity currentLoggedInUser = loggedInUserProvider.getCurrentLoggedInUser();
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name(currentLoggedInUser.getName())
                .owner(currentLoggedInUser)
                .build();
        projectEntity = projectRepository.save(projectEntity);
        return projectMapper.toProjectResponseDTO(projectEntity);
    }

    @Override
    public ProjectResponseDTO updateProjectById(Long projectId, ProjectRequestDTO projectRequestDTO) {
        return null;
    }

    @Override
    public Void deleteProjectById(Long projectId) {
        return null;
    }

}
