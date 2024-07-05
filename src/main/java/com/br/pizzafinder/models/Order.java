package com.br.pizzafinder.models;

import java.util.LinkedHashSet;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import com.br.pizzafinder.controllers.OrderController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "order_tb")
public class Order {
    
    @Id
    @GeneratedValue(
        generator = "order_seq",
        strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
        name = "order_seq",
        sequenceName = "order_seq",
        allocationSize = 1
    )
    @Column(name = "order_id")
    private Long id;

    @NotBlank
    @Column(name = "order_address")
    private String address;

    @ManyToOne(
        fetch = FetchType.EAGER,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        }
    )
    @JoinColumn(
        name = "restaurant_id",
        referencedColumnName = "restaurant_id",
        foreignKey = @ForeignKey(name = "order_restaurant_fk")
    )
    private Restaurant restaurant;

    @ManyToOne(
        fetch = FetchType.EAGER,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        }
    )
    @JoinColumn(
        name = "customer_id",
        referencedColumnName = "user_id",
        foreignKey = @ForeignKey(name = "customer_fk")
    )
    private User customer;

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        }
    )
    @JoinTable(
        name = "order_pizzas_tb",
        joinColumns = @JoinColumn(
            name = "order_id",
            referencedColumnName = "order_id",
            foreignKey = @ForeignKey(name = "order_fk")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "pizza_id",
            referencedColumnName = "pizza_id",
            foreignKey = @ForeignKey(name = "order_pizzas_fk")
        )
    )
    private LinkedHashSet<Pizza> pizzas;
    
    @Column(name = "order_total_price")
    private float totalPrice;

    public Order() {}
    
    public Order(@NotBlank String address, Restaurant restaurant, User customer) {
        this.address = address;
        this.restaurant = restaurant;
        this.customer = customer;
        this.totalPrice = 0;

        if (pizzas != null) {
            pizzas.forEach(pizza -> this.totalPrice += pizza.getPrice());
        }
    }

    public EntityModel<Order> toModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(OrderController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(OrderController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public LinkedHashSet<Pizza> getPizzas() {
        return pizzas;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", address=" + address + ", customer=" + customer + ", pizzas=" + pizzas + "]";
    }
}
