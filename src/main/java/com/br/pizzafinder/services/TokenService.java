package com.br.pizzafinder.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.br.pizzafinder.models.UserLoginCredentials;

@Service
public class TokenService {
    public String generateToken(UserLoginCredentials credentials) {
        String token = JWT.create()
            .withSubject(credentials.email())
            .withIssuer("Pizzafinder")
            .withExpiresAt(Instant.now().plus(2, ChronoUnit.MONTHS))
            .sign(Algorithm.HMAC256("secret"));

        return token;
    }

    public String validateToken(String token) {
        return JWT.require(Algorithm.HMAC256("secret"))
            .withIssuer("Pizzafinder")
            .build()
            .verify(token)
            .getSubject();
    }
}
