package com.br.pizzafinder.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.pizzafinder.models.Pizza;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    Page<Pizza> findByName(Pageable pageable, String query);
}
