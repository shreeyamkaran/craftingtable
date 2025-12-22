package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.requests.SignInRequestDTO;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.AuthResponseDTO;
import com.karan.craftingtable.services.AuthService;
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
    public ResponseEntity<AuthResponseDTO> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO){
        return new ResponseEntity<>(authService.signUp(signUpRequestDTO), HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponseDTO> signIn(@RequestBody SignInRequestDTO signInRequestDTO){
        return new ResponseEntity<>(authService.signIn(signInRequestDTO), HttpStatus.OK);
    }

}
