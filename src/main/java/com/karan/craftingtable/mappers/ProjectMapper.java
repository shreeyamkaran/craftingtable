package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.models.responses.ProjectResponseDTO;
import com.karan.craftingtable.models.responses.ProjectSummaryResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponseDTO toProjectResponseDTO(ProjectEntity projectEntity);

    ProjectSummaryResponseDTO toProjectSummaryResponseDTO(ProjectEntity projectEntity);

    List<ProjectSummaryResponseDTO> toProjectSummaryResponseDTOList(List<ProjectEntity> projectEntityList);

}
