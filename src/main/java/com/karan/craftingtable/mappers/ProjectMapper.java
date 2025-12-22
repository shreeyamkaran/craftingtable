package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponseDTO toProjectResponseDTO(ProjectEntity projectEntity);

}
