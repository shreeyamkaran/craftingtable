package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.FileContentResponseDTO;
import com.karan.craftingtable.models.responses.FileNodeResponseDTO;

import java.util.List;

public interface ProjectFileService {

    List<FileNodeResponseDTO> getFileTree(Long projectId);

    FileContentResponseDTO getFile(Long projectId);

    void saveFile(Long projectId, String filePath, String fileContent);

}
