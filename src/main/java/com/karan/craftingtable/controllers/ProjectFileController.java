package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.responses.FileContentResponseDTO;
import com.karan.craftingtable.models.responses.FileResponseDTO;
import com.karan.craftingtable.services.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/files")
public class ProjectFileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<List<FileResponseDTO>> getFileTree(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectFileService.getFileTree(projectId), HttpStatus.OK);
    }

    @GetMapping("/{*path}")
    public ResponseEntity<FileContentResponseDTO> getFile(@PathVariable Long projectId, @PathVariable String path) {
        return new ResponseEntity<>(projectFileService.getFile(projectId), HttpStatus.OK);
    }

}
