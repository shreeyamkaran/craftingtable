package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.models.requests.SignInRequestDTO;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.AuthResponseDTO;
import com.karan.craftingtable.services.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplementation implements AuthService {

    @Override
    public AuthResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        return null;
    }

    @Override
    public AuthResponseDTO signIn(SignInRequestDTO signInRequestDTO) {
        return null;
    }

}
