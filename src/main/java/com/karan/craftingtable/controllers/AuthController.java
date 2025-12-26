package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.SignInRequestDTO;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.AuthResponseDTO;
import com.karan.craftingtable.models.responses.UserProfileResponseDTO;
import com.karan.craftingtable.models.wrappers.APIResponse;
import com.karan.craftingtable.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<APIResponse<UserProfileResponseDTO>> signUp(
            @Valid @RequestBody SignUpRequestDTO signUpRequestDTO
    ){
        UserProfileResponseDTO response = authService.signUp(signUpRequestDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<APIResponse<AuthResponseDTO>> signIn(
            @Valid @RequestBody SignInRequestDTO signInRequestDTO,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ){
        String[] tokens = authService.signIn(signInRequestDTO);
        Cookie cookie = new Cookie("refresh_token", tokens[1]);
        cookie.setHttpOnly(true);
        httpServletResponse.addCookie(cookie);
        AuthResponseDTO response = new AuthResponseDTO(tokens[0]);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(APIResponse.success(response));
    }

}
