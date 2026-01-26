package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.ProjectFileEntity;
import com.karan.craftingtable.models.responses.FileNodeResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNodeResponseDTO> toFileNodeResponseDTOList(List<ProjectFileEntity> projectFileList);

}
