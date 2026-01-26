package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ProjectFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectFileRepository extends JpaRepository<ProjectFileEntity, Long> {

    Optional<ProjectFileEntity> findByProjectIdAndPath(Long projectId, String cleanPath);

    List<ProjectFileEntity> findByProjectId(Long projectId);

}
