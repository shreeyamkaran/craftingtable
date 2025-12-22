package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.responses.FileContentResponseDTO;
import com.karan.craftingtable.models.responses.FileResponseDTO;
import com.karan.craftingtable.services.ProjectFileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectFileServiceImplementation implements ProjectFileService {

    @Override
    public List<FileResponseDTO> getFileTree(Long projectId) {
        return List.of();
    }

    @Override
    public FileContentResponseDTO getFile(Long projectId) {
        return null;
    }

}
