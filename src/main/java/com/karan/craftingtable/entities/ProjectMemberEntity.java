package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.ProjectMemberRoleEnum;

import java.time.Instant;

public class ProjectMemberEntity {

    private ProjectEntity id;
    private ProjectEntity project;
    private UserEntity member;
    private ProjectMemberRoleEnum projectMemberRole;
    private Instant invitedAt;
    private Instant inviteAcceptedAt;

}

class ProjectMemberEntityId {
    private Long projectId;
    private Long userId;
}

