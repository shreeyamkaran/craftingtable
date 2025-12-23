package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.ProjectRequestDTO;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import com.karan.craftingtable.models.responses.ProjectSummaryResponseDTO;
import com.karan.craftingtable.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/all")
    public ResponseEntity<List<ProjectSummaryResponseDTO>> getAllProjects() {
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO projectRequestDTO) {
        return new ResponseEntity<>(projectService.createProject(projectRequestDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> updateProjectById(@PathVariable Long projectId, @RequestBody ProjectRequestDTO projectRequestDTO) {
        return new ResponseEntity<>(projectService.updateProjectById(projectId, projectRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> softDeleteProjectById(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.softDeleteProjectById(projectId), HttpStatus.NO_CONTENT);
    }

}
