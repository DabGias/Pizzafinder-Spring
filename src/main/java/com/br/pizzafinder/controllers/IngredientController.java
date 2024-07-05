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

import com.br.pizzafinder.models.Ingredient;
import com.br.pizzafinder.repositories.IngredientRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    
    @Autowired
    IngredientRepository repo;

    @Autowired
    PagedResourcesAssembler<Ingredient> assembler;

    @GetMapping
    public PagedModel<EntityModel<Ingredient>> index(@PageableDefault(size = 50) Pageable pageable, String query) {
        return assembler.toModel(query == null || query.equals("") ? repo.findAll(pageable) : repo.findByName(pageable, query));
    }

    @GetMapping("{id}")
    public EntityModel<Ingredient> show(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado!")
        ).toNoSelfModel();
    }

    @PostMapping
    public ResponseEntity<EntityModel<Ingredient>> create(@RequestBody @Valid Ingredient ingredient) {
        repo.save(ingredient);

        return ResponseEntity.created(
            ingredient.toModel().getRequiredLink("self").toUri()
        ).body(ingredient.toModel());
    }

    @PutMapping("{id}")
    public ResponseEntity<EntityModel<Ingredient>> update(@PathVariable Long id, @RequestBody @Valid Ingredient ingredient) {
        Ingredient i = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado!")
        );

        BeanUtils.copyProperties(ingredient, i, "id");

        repo.save(i);

        return ResponseEntity.ok(i.toModel());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Ingredient> destroy(@PathVariable Long id) {
        Ingredient ingredient = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado!")
        );

        repo.delete(ingredient);

        return ResponseEntity.noContent().build();
    }
}
