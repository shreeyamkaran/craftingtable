package com.karan.craftingtable.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "t_projects",
        indexes = {
                @Index(name = "idx_projects_last_modified_at_desc_deleted_at", columnList = "last_modified_at DESC, deleted_at"),
                @Index(name = "idx_projects_deleted_at_last_modified_at_desc", columnList = "deleted_at, last_modified_at DESC"),
                @Index(name = "idx_projects_deleted_at", columnList = "deleted_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectEntity extends AuditableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    private Boolean isPublic = false;

}
