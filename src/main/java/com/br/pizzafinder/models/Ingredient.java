package com.br.pizzafinder.models;

import org.springframework.hateoas.EntityModel;

import com.br.pizzafinder.controllers.IngredientController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.data.domain.Pageable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(
    name = "ingredient_tb",
    uniqueConstraints = @UniqueConstraint(
        name = "ingredient_name_uk",
        columnNames = "ingredient_name"
    )
)
public class Ingredient {
    
    @Id
    @GeneratedValue(
        generator = "ingredient_seq",
        strategy = GenerationType.SEQUENCE 
    )
    @SequenceGenerator(
        name = "ingredient_seq",
        sequenceName = "ingredient_seq",
        allocationSize = 1
    )
    @Column(name = "ingredient_id")
    private Long id;

    @NotBlank
    @Size(min = 3)
    @Column(name = "ingredient_name")
    private String name;

    public Ingredient() {}

    public Ingredient(@NotBlank @Size(min = 3) String name) {
        this.name = name;
    }

    public EntityModel<Ingredient> toModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(IngredientController.class).show(id)).withSelfRel(),
            linkTo(methodOn(IngredientController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(IngredientController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public EntityModel<Ingredient> toNoSelfModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(IngredientController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(IngredientController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ingredient [id=" + id + ", name=" + name + "]";
    }
}
