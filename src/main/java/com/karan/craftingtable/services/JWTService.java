package com.karan.craftingtable.services;

import com.karan.craftingtable.entities.UserEntity;

public interface JWTService {

    String generateAccessToken(UserEntity userEntity);

    String generateRefreshToken(UserEntity userEntity);

    Long getUserIdFromToken(String token);

}
