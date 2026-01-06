package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.InviteProjectMemberRequestDTO;
import com.karan.craftingtable.models.requests.RespondToInviteRequestDTO;
import com.karan.craftingtable.models.requests.UpdateProjectMemberRoleRequestDTO;
import com.karan.craftingtable.models.responses.ProjectMemberResponseDTO;
import com.karan.craftingtable.models.responses.RespondToInviteResponseDTO;
import com.karan.craftingtable.models.wrappers.APIResponse;
import com.karan.craftingtable.services.ProjectMemberService;
import jakarta.validation.Valid;
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
    public ResponseEntity<APIResponse<List<ProjectMemberResponseDTO>>> getProjectMembers(
            @PathVariable Long projectId
    ) {
        List<ProjectMemberResponseDTO> response = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @PostMapping("/invite")
    public ResponseEntity<APIResponse<ProjectMemberResponseDTO>> inviteProjectMember(
            @PathVariable Long projectId,
            @Valid @RequestBody InviteProjectMemberRequestDTO inviteProjectMemberRequestDTO
    ) {
        ProjectMemberResponseDTO response = projectMemberService.inviteProjectMember(projectId, inviteProjectMemberRequestDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @PatchMapping
    public ResponseEntity<APIResponse<ProjectMemberResponseDTO>> updateProjectMemberRole(
            @PathVariable Long projectId,
            @Valid @RequestBody UpdateProjectMemberRoleRequestDTO updateProjectMemberRoleRequestDTO
    ) {
        ProjectMemberResponseDTO response =  projectMemberService.updateProjectMemberRole(projectId, updateProjectMemberRoleRequestDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @DeleteMapping("/{projectMemberId}")
    public ResponseEntity<APIResponse<Void>> removeProjectMember(
            @PathVariable Long projectId,
            @PathVariable Long projectMemberId
    ) {
        Void response = projectMemberService.removeProjectMember(projectId, projectMemberId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(APIResponse.success(response));
    }

    @PostMapping("/invite/respond")
    public ResponseEntity<APIResponse<RespondToInviteResponseDTO>> respondToInvite(
            @PathVariable Long projectId,
            @Valid @RequestBody RespondToInviteRequestDTO respondToInviteRequestDTO
    ) {
        RespondToInviteResponseDTO response = projectMemberService.respondToInvite(projectId, respondToInviteRequestDTO);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(APIResponse.success(response));
    }

}
