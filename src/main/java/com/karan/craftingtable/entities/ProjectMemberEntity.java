package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "t_project_members",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "project_member_id"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberEntity {

    // composite key (a static inner class. defined at the bottom)
    @EmbeddedId
    private ProjectMemberEntityId id;

    @ManyToOne(optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne(optional = false)
    @MapsId("projectMemberId")
    @JoinColumn(name = "project_member_id", nullable = false)
    private UserEntity projectMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectMemberRoleEnum projectMemberRole = ProjectMemberRoleEnum.VIEWER;

    private Instant invitedAt;

    private Instant inviteAcceptedAt;

    // composite key class definition
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ProjectMemberEntityId implements Serializable {

        private Long projectId;

        private Long projectMemberId;

    }

}

