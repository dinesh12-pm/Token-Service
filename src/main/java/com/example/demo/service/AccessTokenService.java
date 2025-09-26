package com.example.demo.service;

import com.example.demo.entity.AccessToken;
import com.example.demo.repository.AccessTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Service
public class AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;

    public AccessTokenService(AccessTokenRepository accessTokenRepository) {
        this.accessTokenRepository = accessTokenRepository;
    }


    public String generateAccessToken(String userName) {
    String token = UUID.randomUUID().toString();
    try{
        AccessToken accessToken = AccessToken.builder()
                .token(token)
                .expiresAt(Instant.now())
                .userName(userName)
                .build();
        accessTokenRepository.save(accessToken);
        return token;
    }

catch (Exception e){
        throw new RuntimeException("Error while creating opaque Token.....");
}
    }
}
