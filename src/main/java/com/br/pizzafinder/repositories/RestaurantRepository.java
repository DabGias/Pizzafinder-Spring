package com.br.pizzafinder.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.pizzafinder.models.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findByName(Pageable pageable, String query);
    Page<Restaurant> findByAddress(Pageable pageable, String query);
}
