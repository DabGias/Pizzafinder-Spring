package com.br.pizzafinder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.br.pizzafinder.repositories.UserRepository;

public class AuthenticationService implements UserDetailsService {
    
    @Autowired
    UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findByEmail(username).orElseThrow(
            () -> new UsernameNotFoundException("Usuário não encontrado!")
        );
    }
}
