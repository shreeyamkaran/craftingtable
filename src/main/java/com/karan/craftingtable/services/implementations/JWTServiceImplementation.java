package com.karan.craftingtable.services.implementations;

import com.karan.craftingtable.configurations.PropertiesConfiguration;
import com.karan.craftingtable.entities.UserEntity;
import com.karan.craftingtable.services.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JWTServiceImplementation implements JWTService {

    private final PropertiesConfiguration propertiesConfiguration;

    @Override
    public SecretKey getSecretKey() {
        String jwtSecretHex = propertiesConfiguration.getJwtSecret();
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretHex);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(UserEntity userEntity) {
        Long accessTokenExpirationMs = propertiesConfiguration.getAccessTokenExpirationMs();
        return Jwts.builder()
                .subject(userEntity.getId().toString())
                .claim("token_type", "access_token")
                .claim("email", userEntity.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(getSecretKey(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserEntity userEntity) {
        Long refreshTokenExpirationMs = propertiesConfiguration.getRefreshTokenExpirationMs();
        return Jwts.builder()
                .subject(userEntity.getId().toString())
                .claim("token_type", "refresh_token")
                .claim("email", userEntity.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(getSecretKey(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.valueOf(claims.getSubject());
    }

}
