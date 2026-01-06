package com.karan.craftingtable.services;

import com.karan.craftingtable.entities.UserEntity;

import javax.crypto.SecretKey;

public interface JWTService {

    SecretKey getSecretKey();

    String generateAccessToken(UserEntity userEntity);

    String generateRefreshToken(UserEntity userEntity);

    Long getUserIdFromToken(String token);

}
