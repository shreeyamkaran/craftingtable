package com.karan.craftingtable.utilities;

import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggedInUserProvider {

    private final UserRepository userRepository;

    public UserEntity getCurrentLoggedInUser() {
        return userRepository.findById(1L).orElseThrow();
    }

}
