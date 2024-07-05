package com.br.pizzafinder.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.br.pizzafinder.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByAddress(Pageable pageable, String query);
}
