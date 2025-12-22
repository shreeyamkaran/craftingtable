package com.karan.craftingtable.services;

import com.karan.craftingtable.models.requests.SignInRequestDTO;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);

    AuthResponseDTO signIn(SignInRequestDTO signInRequestDTO);

}
