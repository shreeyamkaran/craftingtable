package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.requests.ProjectRequestDTO;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import com.karan.craftingtable.models.responses.ProjectSummaryResponseDTO;
import com.karan.craftingtable.services.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImplementation implements ProjectService {

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
        return null;
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
