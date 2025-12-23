package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.ProjectMemberEntity;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "id", source = "projectMember.id")
    @Mapping(target = "name", source = "projectMember.name")
    @Mapping(target = "email", source = "projectMember.email")
    @Mapping(target = "avatarURL", source = "projectMember.avatarURL")
    ProjectMemberResponseDTO toProjectMemberResponseDTOFromProjectMemberEntity(
            ProjectMemberEntity projectMemberEntity
    );

    List<ProjectMemberResponseDTO> toProjectMemberResponseDTOListFromProjectMemberEntityList(
            List<ProjectMemberEntity> projectMemberEntityList
    );

    @Mapping(target = "projectMemberRole", constant = "OWNER")
    @Mapping(target = "invitedAt", ignore = true)
    ProjectMemberResponseDTO toProjectMemberResponseDTOFromProjectOwnerUserEntity(
            UserEntity projectOwner
    );

}
