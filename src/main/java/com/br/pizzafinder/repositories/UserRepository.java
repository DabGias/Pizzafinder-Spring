package com.br.pizzafinder.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.pizzafinder.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String query);
    Page<User> findByEmail(Pageable pageable, String query);
}
