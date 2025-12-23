package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("""
        SELECT p FROM ProjectEntity p\s
        WHERE p.deletedAt IS NULL\s
        AND p.owner.id = :userId\s
        ORDER BY p.lastModifiedAt DESC\s
    """)
    List<ProjectEntity> findAllAccessibleProjects(@Param("userId") Long userId);

    @Query("""
        SELECT p FROM ProjectEntity p\s
        LEFT JOIN FETCH p.owner\s
        WHERE p.id = :projectId\s
        AND p.deletedAt IS NULL\s
        AND p.owner.id = :userId\s
    """)
    Optional<ProjectEntity> findAccessibleProjectById(@Param("projectId") Long projectId, @Param("userId") Long userId);

}
