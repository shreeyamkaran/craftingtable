package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ProjectMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberEntity.ProjectMemberEntityId> {

    List<ProjectMemberEntity> findByIdProjectIdAndInviteAcceptedAtIsNotNull(Long projectId);

}
