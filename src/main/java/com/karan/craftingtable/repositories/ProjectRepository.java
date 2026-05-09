package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ProjectEntity;
import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("""
        SELECT p as project, pm.projectMemberRole as role
        FROM ProjectEntity p
        JOIN ProjectMemberEntity pm ON pm.project.id = p.id
        WHERE pm.projectMember.id = :userId
        AND p.deletedAt IS NULL
        ORDER BY p.lastModifiedAt DESC
    """)
    List<ProjectWithRole> findAllAccessibleProjects(@Param("userId") Long userId);

    @Query("""
        SELECT p FROM ProjectEntity p\s
        WHERE p.id = :projectId\s
        AND EXISTS(
            SELECT 1 FROM ProjectMemberEntity pm\s
            WHERE pm.id.projectMemberId = :userId\s
            AND pm.id.projectId = :projectId
        )\s
    """)
    Optional<ProjectEntity> findAccessibleProjectById(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );

    @Query("""
        SELECT p as project, pm.projectMemberRole as role\s
        FROM ProjectEntity p\s
        JOIN ProjectMemberEntity pm ON pm.project.id = p.id\s
        WHERE p.id = :projectId\s
        AND pm.projectMember.id = :userId\s
        AND p.deletedAt IS NULL\s
    """)
    Optional<ProjectWithRole> findAccessibleProjectByIdWithRole(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );

    interface ProjectWithRole {
        ProjectEntity getProject();
        ProjectMemberRoleEnum getRole();
    }

}
