package com.karan.craftingtable.controllers;

import com.karan.craftingtable.models.responses.UserProfileResponseDTO;
import com.karan.craftingtable.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getProfile(){
        return new ResponseEntity<>(userService.getProfile(), HttpStatus.OK);
    }

}
