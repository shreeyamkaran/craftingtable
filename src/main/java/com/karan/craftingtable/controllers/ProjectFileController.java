package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.responses.FileContentResponseDTO;
import com.karan.craftingtable.models.responses.FileNodeResponseDTO;
import com.karan.craftingtable.models.wrappers.APIResponse;
import com.karan.craftingtable.services.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/files")
public class ProjectFileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<APIResponse<List<FileNodeResponseDTO>>> getFileTree(
            @PathVariable Long projectId
    ) {
        List<FileNodeResponseDTO> response = projectFileService.getFileTree(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));

    }

    @GetMapping("/content")
    public ResponseEntity<APIResponse<FileContentResponseDTO>> getFile(
            @PathVariable Long projectId,
            @RequestParam String path
    ) {
        FileContentResponseDTO response = projectFileService.getFileContent(projectId, path);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

}
