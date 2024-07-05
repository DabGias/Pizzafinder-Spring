package com.br.pizzafinder.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.br.pizzafinder.models.Order;
import com.br.pizzafinder.repositories.OrderRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderRepository repo;

    @Autowired
    PagedResourcesAssembler<Order> assembler;

    @GetMapping
    public PagedModel<EntityModel<Order>> index(@PageableDefault(size = 20) Pageable pageable, String query) {
        return assembler.toModel(query == null || query.equals("") ? repo.findAll(pageable) : repo.findByAddress(pageable, query));
    }

    @GetMapping("{id}")
    public EntityModel<Order> show(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado!")
        ).toModel();
    }

    @PostMapping
    public ResponseEntity<EntityModel<Order>> create(@RequestBody @Valid Order order) {
        repo.save(order);

        return ResponseEntity.created(
            order.toModel().getRequiredLink("self").toUri()
        ).body(order.toModel());
    }

    @PutMapping("{id}")
    public ResponseEntity<EntityModel<Order>> update(@PathVariable Long id, @RequestBody @Valid Order order) {
        Order o = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado!")
        );

        BeanUtils.copyProperties(order, o, "id");

        repo.save(o);

        return ResponseEntity.ok(o.toModel());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Order> destroy(@PathVariable Long id) {
        Order order = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado!")
        );

        repo.delete(order);

        return ResponseEntity.noContent().build();
    }
}
