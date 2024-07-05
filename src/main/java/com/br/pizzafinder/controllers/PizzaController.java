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
import com.br.pizzafinder.models.Pizza;
import com.br.pizzafinder.models.PizzaIngredientsRequestObject;
import com.br.pizzafinder.repositories.IngredientRepository;
import com.br.pizzafinder.repositories.PizzaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pizzas")
public class PizzaController {
    
    @Autowired
    PizzaRepository repo;

    @Autowired
    IngredientRepository ingredientRepo;

    @Autowired
    PagedResourcesAssembler<Pizza> assembler;

    @GetMapping
    public PagedModel<EntityModel<Pizza>> index(@PageableDefault(size = 20) Pageable pageable, String query) {
        return assembler.toModel(query == null || query.equals("") ? repo.findAll(pageable) : repo.findByName(pageable, query));
    }

    @GetMapping("{id}")
    public EntityModel<Pizza> show(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza não encontrada!")
        ).toNoSelfModel();
    }

    @PostMapping
    public ResponseEntity<EntityModel<Pizza>> create(@RequestBody @Valid Pizza pizza) {
        for (Ingredient ingredient : pizza.getIngredients()) {
            try {
                Ingredient i = ingredientRepo.findByName(ingredient.getName()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado!")
                );

                ingredient.setId(i.getId());
            } catch (ResponseStatusException e) {
                ingredientRepo.save(ingredient);

                ingredient = ingredientRepo.findByName(ingredient.getName()).get();
            }
        }
        
        repo.save(pizza);

        return ResponseEntity.created(
            pizza.toModel().getRequiredLink("self").toUri()
        ).body(pizza.toModel());
    }

    @PostMapping("{id}/add-ingredient")
    public ResponseEntity<EntityModel<Pizza>> addIngredient(@PathVariable Long id, @RequestBody PizzaIngredientsRequestObject requestObject) {
        Pizza pizza = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza não encontrada!")
        );

        for (String ingredient : requestObject.getIngredients()) {
            Ingredient i = ingredientRepo.findByName(ingredient).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado!")
            );

            pizza.getIngredients().add(i);
        }

        repo.save(pizza);

        return ResponseEntity.ok(pizza.toAddIngredientModel());
    }

    @PostMapping("{id}/rmv-ingredient")
    public ResponseEntity<EntityModel<Pizza>> rmvIngredient(@PathVariable Long id, @RequestBody PizzaIngredientsRequestObject requestObject) {
        Pizza pizza = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza não encontrada!")
        );

        for (String ingredient : requestObject.getIngredients()) {
            Ingredient i = ingredientRepo.findByName(ingredient).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente não encontrado!")
            );

            pizza.getIngredients().remove(i);
        }

        repo.save(pizza);

        return ResponseEntity.ok(pizza.toRmvIngredientModel());
    }

    @PutMapping("{id}")
    public ResponseEntity<EntityModel<Pizza>> update(@PathVariable Long id, @RequestBody @Valid Pizza pizza) {
        Pizza p = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza não encontrada!")
        );

        BeanUtils.copyProperties(pizza, p, "id");

        repo.save(p);

        return ResponseEntity.ok(p.toModel());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Pizza> destroy(@PathVariable Long id) {
        Pizza pizza = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pizza não encontrada!")
        );

        repo.delete(pizza);

        return ResponseEntity.noContent().build();
    }
}
