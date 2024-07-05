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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.br.pizzafinder.models.Restaurant;
import com.br.pizzafinder.repositories.RestaurantRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    
    @Autowired
    RestaurantRepository repo;

    @Autowired
    PagedResourcesAssembler<Restaurant> assembler;

    @GetMapping
    public PagedModel<EntityModel<Restaurant>> index(@PageableDefault(size = 20) Pageable pageable, @RequestParam(required = false) String query) {
        return assembler.toModel(query == null || query.equals("") ? repo.findAll(pageable) : repo.findByName(pageable, query));
    }

    @GetMapping("{id}")
    public EntityModel<Restaurant> show(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado!")
        ).toNoSelfModel();
    }

    @PostMapping
    public ResponseEntity<EntityModel<Restaurant>> create(@RequestBody @Valid Restaurant restaurant) {
        repo.save(restaurant);

        return ResponseEntity.created(
            restaurant.toModel().getRequiredLink("self").toUri()
        ).body(restaurant.toModel());
    }

    @PutMapping("{id}")
    public ResponseEntity<EntityModel<Restaurant>> update(@PathVariable Long id, @RequestBody @Valid Restaurant restaurant) {
        Restaurant r = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado!")
        );

        BeanUtils.copyProperties(restaurant, r, "id");

        repo.save(r);

        return ResponseEntity.ok(r.toModel());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Restaurant> destroy(@PathVariable Long id) {
        Restaurant restaurant = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado!")
        );

        repo.delete(restaurant);

        return ResponseEntity.noContent().build();
    }
}
