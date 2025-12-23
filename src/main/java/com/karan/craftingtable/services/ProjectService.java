package com.karan.craftingtable.services;

import com.karan.craftingtable.models.requests.ProjectRequestDTO;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import com.karan.craftingtable.models.responses.ProjectSummaryResponseDTO;

import java.util.List;

public interface ProjectService {

    List<ProjectSummaryResponseDTO> getAllProjects();

    ProjectResponseDTO getProjectById(Long projectId);

    ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO);

    ProjectResponseDTO updateProjectById(Long projectId, ProjectRequestDTO projectRequestDTO);

    Void softDeleteProjectById(Long projectId);

}
