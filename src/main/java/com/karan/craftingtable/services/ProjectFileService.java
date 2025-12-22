package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.FileContentResponseDTO;
import com.karan.craftingtable.models.responses.FileResponseDTO;

import java.util.List;

public interface ProjectFileService {

    List<FileResponseDTO> getFileTree(Long projectId);

    FileContentResponseDTO getFile(Long projectId);

}
