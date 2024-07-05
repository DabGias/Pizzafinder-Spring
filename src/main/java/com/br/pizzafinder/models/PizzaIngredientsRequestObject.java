package com.br.pizzafinder.models;

import java.io.Serializable;
import java.util.List;

public class PizzaIngredientsRequestObject implements Serializable {
    private List<String> ingredients;

    public PizzaIngredientsRequestObject() {}

    public PizzaIngredientsRequestObject(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "PizzaIngredientsRequestObject [ingredients=" + ingredients + "]";
    }
}
