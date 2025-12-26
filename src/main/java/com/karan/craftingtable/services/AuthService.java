package com.karan.craftingtable.services;

import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.models.requests.SignInRequestDTO;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.UserProfileResponseDTO;

public interface AuthService {

    UserProfileResponseDTO signUp(SignUpRequestDTO signUpRequestDTO);

    String[] signIn(SignInRequestDTO signInRequestDTO);

    UserEntity getCurrentLoggedInUser();

}
