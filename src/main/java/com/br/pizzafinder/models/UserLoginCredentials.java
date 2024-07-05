package com.br.pizzafinder.models;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

public record UserLoginCredentials(String email, String password) {
    public Authentication toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
