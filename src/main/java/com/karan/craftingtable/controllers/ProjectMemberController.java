package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import com.karan.craftingtable.services.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping("/all")
    public ResponseEntity<List<ProjectMemberResponseDTO>> getProjectMembers(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectMemberService.getProjectMembers(projectId), HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<ProjectMemberResponseDTO> inviteProjectMember(@PathVariable Long projectId, @RequestBody InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO) {
        return new ResponseEntity<>(projectMemberService.inviteProjectMember(projectId, inviteProjectMemberRequestDTO), HttpStatus.OK);
    }

    @PatchMapping("/{projectMemberId}")
    public ResponseEntity<ProjectMemberResponseDTO> updateProjectMemberRole(@PathVariable Long projectId, @PathVariable Long projectMemberId, @RequestBody UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO) {
        return new ResponseEntity<>(projectMemberService.updateProjectMemberRole(projectId, projectMemberId, updateProjectMemberRoleRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{projectMemberId}")
    public ResponseEntity<Void> deleteProjectMember(@PathVariable Long projectId, @PathVariable Long projectMemberId) {
        return new ResponseEntity<>(projectMemberService.deleteProjectMember(projectId, projectMemberId), HttpStatus.NO_CONTENT);
    }

}
