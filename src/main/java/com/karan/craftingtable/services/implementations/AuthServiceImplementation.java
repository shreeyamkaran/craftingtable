package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.exceptions.BadRequestException;
import com.karan.craftingtable.exceptions.UnauthenticatedException;
import com.karan.craftingtable.exceptions.UnauthorizedException;
import com.karan.craftingtable.mappers.UserMapper;
import com.karan.craftingtable.models.requests.SignInRequestDTO;
import com.karan.craftingtable.models.requests.SignUpRequestDTO;
import com.karan.craftingtable.models.responses.UserProfileResponseDTO;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.AuthService;
import com.karan.craftingtable.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public UserProfileResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        userRepository.findByEmail(signUpRequestDTO.email()).ifPresent(user -> {
            throw new BadRequestException("Email already exists");
        });
        UserEntity userEntity = userMapper.toUserEntity(signUpRequestDTO);
        String hashedPassword = passwordEncoder.encode(signUpRequestDTO.password());
        userEntity.setPassword(hashedPassword);
        return userMapper.toUserProfileResponseDTO(userRepository.save(userEntity));
    }

    @Override
    public String[] signIn(SignInRequestDTO signInRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDTO.email(), signInRequestDTO.password())
        );
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);
        return new String[]{accessToken, refreshToken};
    }

    @Override
    public UserEntity getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthenticatedException("No authenticated user found");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity userEntity) {
            return userEntity;
        }
        throw new UnauthorizedException("Invalid authentication principal");
    }

}
