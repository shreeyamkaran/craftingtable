package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.exceptions.ResourceNotFoundException;
import com.karan.craftingtable.models.responses.UserProfileResponseDTO;
import com.karan.craftingtable.repositories.UserRepository;
import com.karan.craftingtable.services.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponseDTO getProfile() {
        return null;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) {
        return userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
    }

}
