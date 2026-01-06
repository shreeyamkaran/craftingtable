package com.karan.craftingtable.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.karan.craftingtable.enums.PermissionEnum.*;

@RequiredArgsConstructor
@Getter
public enum ProjectMemberRoleEnum {

    OWNER(Set.of(
            VIEW_PROJECT,
            EDIT_PROJECT,
            DELETE_PROJECT,
            VIEW_PROJECT_MEMBERS,
            INVITE_PROJECT_MEMBERS,
            UPDATE_PROJECT_MEMBERS,
            REMOVE_PROJECT_MEMBERS
    )),
    EDITOR(Set.of(
            VIEW_PROJECT,
            EDIT_PROJECT,
            VIEW_PROJECT_MEMBERS,
            INVITE_PROJECT_MEMBERS
    )),
    VIEWER(Set.of(
            VIEW_PROJECT,
            VIEW_PROJECT_MEMBERS
    )),;

    private final Set<PermissionEnum> permissions;

}
