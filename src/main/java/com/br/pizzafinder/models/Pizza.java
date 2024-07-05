package com.br.pizzafinder.models;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import com.br.pizzafinder.controllers.PizzaController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "pizza_tb")
public class Pizza {
    
    @Id
    @GeneratedValue(
        generator = "pizza_seq",
        strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
        name = "pizza_seq",
        sequenceName = "pizza_seq",
        allocationSize = 1
    )
    @Column(name = "pizza_id")
    private Long id;
    
    @NotNull
    @Column(name = "pizza_name")
    private String name;

    @NotNull
    @Column(name = "pizza_price")
    private float price;

    @NotBlank
    @Column(name = "pizza_description")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "pizza_ingredients_tb",
        joinColumns = @JoinColumn(
            name = "pizza_id",
            referencedColumnName = "pizza_id",
            foreignKey = @ForeignKey(name = "pizza_fk")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "ingredient_id",
            referencedColumnName = "ingredient_id",
            foreignKey = @ForeignKey(name = "pizza_ingredients_fk")
        )
    )
    private Set<Ingredient> ingredients = new LinkedHashSet<>();

    public Pizza() {}

    public Pizza(@NotNull String name, @NotNull float price, @NotBlank String description, Set<Ingredient> ingredients) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
    }

    public EntityModel<Pizza> toModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(PizzaController.class).show(id)).withSelfRel(),
            linkTo(methodOn(PizzaController.class).addIngredient(id, null)).withRel("add ingredients"),
            linkTo(methodOn(PizzaController.class).rmvIngredient(id, null)).withRel("remove ingredients"),
            linkTo(methodOn(PizzaController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(PizzaController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public EntityModel<Pizza> toNoSelfModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(PizzaController.class).addIngredient(id, null)).withRel("add ingredients"),
            linkTo(methodOn(PizzaController.class).rmvIngredient(id, null)).withRel("remove ingredients"),
            linkTo(methodOn(PizzaController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(PizzaController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public EntityModel<Pizza> toAddIngredientModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(PizzaController.class).show(id)).withSelfRel(),
            linkTo(methodOn(PizzaController.class).rmvIngredient(id, null)).withRel("remove ingredients"),
            linkTo(methodOn(PizzaController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(PizzaController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public EntityModel<Pizza> toRmvIngredientModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(PizzaController.class).show(id)).withSelfRel(),
            linkTo(methodOn(PizzaController.class).addIngredient(id, null)).withRel("add ingredients"),
            linkTo(methodOn(PizzaController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(PizzaController.class).index(Pageable.unpaged(), null)).withRel("list-all")
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        return "Pizza [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description + ", ingredients=" + ingredients + "]";
    }
}
