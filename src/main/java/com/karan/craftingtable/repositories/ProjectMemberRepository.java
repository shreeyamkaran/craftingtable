package com.karan.craftingtable.repositories;

import com.karan.craftingtable.entities.ProjectMemberEntity;
import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMemberEntity, ProjectMemberEntity.ProjectMemberEntityId> {

    List<ProjectMemberEntity> findByIdProjectIdAndInviteAcceptedAtIsNotNull(Long projectId);

    @Query("""
        SELECT pm.projectMemberRole\s
        FROM ProjectMemberEntity pm\s
        WHERE pm.id.projectId = :projectId\s
        AND pm.id.projectMemberId = :projectMemberId\s
    """)
    Optional<ProjectMemberRoleEnum> findProjectMemberRoleByIdProjectIdAndIdProjectMemberId(
            @Param("projectId") Long projectId,
            @Param("projectMemberId") Long projectMemberId
    );

    @Query("""
        SELECT COUNT(pm) FROM ProjectMemberEntity pm\s
        WHERE pm.id.projectMemberId = :userId\s
        AND pm.projectMemberRole = 'OWNER'\s
    """)
    int countProjectOwnedByUser(@Param("userId") Long userId);

}
