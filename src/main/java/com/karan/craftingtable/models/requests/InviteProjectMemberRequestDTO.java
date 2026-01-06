package com.karan.craftingtable.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InviteProjectMemberRequestDTO(
        @Email @NotBlank String email
) { }
