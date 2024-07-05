package com.br.pizzafinder.models;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import com.br.pizzafinder.controllers.RestaurantController;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(
    name = "restaurant_tb",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "restaurant_cnpj_uk",
            columnNames = "restaurant_cnpj"
        ),
        @UniqueConstraint(
            name = "restaurant_address_uk",
            columnNames = "restaurant_address"
        ),
        @UniqueConstraint(
            name = "restaurant_telephone_uk",
            columnNames = "restaurant_telephone"
        )
    }
)
public class Restaurant {
    
    @Id
    @GeneratedValue(
        generator = "restaurant_seq",
        strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
        name = "restaurant_seq",
        sequenceName = "restaurant_seq",
        allocationSize = 1
    )
    @Column(name = "restaurant_id")
    private Long id;

    @NotBlank
    @Column(name = "restaurant_name")
    private String name;

    @NotBlank
    @Pattern(regexp = "^(\\d{2})\\.?(\\d{3})\\.?(\\d{3})\\/?([0-1]{4})\\-?(\\d{2})$")
    @Column(name = "restaurant_cnpj")
    private String cnpj;

    @NotBlank
    @Column(name = "restaurant_address")
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\(?[0-9]{2}\\)?\\s?[0-9]{5}-?[0-9]{4}$")
    @Column(name = "restaurant_telephone")
    private String telephone;

    @ManyToOne(
        fetch = FetchType.EAGER,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        }
    )
    @JoinColumn(
        name = "manager_id",
        referencedColumnName = "user_id",
        foreignKey = @ForeignKey(name = "manager_fk")
    )
    private User manager;

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
        }
    )
    @JoinTable(
        name = "restaurant_pizzas_tb",
        joinColumns = @JoinColumn(
            name = "restaurant_id",
            referencedColumnName = "restaurant_id",
            foreignKey = @ForeignKey(name = "restaurant_fk")
        ),
        inverseJoinColumns = @JoinColumn(
            name = "pizza_id",
            referencedColumnName = "pizza_id",
            foreignKey = @ForeignKey(name = "restaurant_pizzas_fk")
        )
    )
    private Set<Pizza> pizzas = new LinkedHashSet<>();

    public Restaurant() {}

    public Restaurant(@NotBlank String name, @NotBlank @Pattern(regexp = "^(\\d{2})\\.?(\\d{3})\\.?(\\d{3})\\/?([0-1]{4})\\-?(\\d{2})$") String cnpj, @NotBlank String address, @NotBlank @Pattern(regexp = "^\\(?[0-9]{2}\\)?\\s?[0-9]{5}-?[0-9]{4}$") String telephone, User manager) {
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
        this.telephone = telephone;
        this.manager = manager;
    }

    public Restaurant(@NotBlank String name, @NotBlank @Pattern(regexp = "^(\\d{2})\\.?(\\d{3})\\.?(\\d{3})\\/?([0-1]{4})\\-?(\\d{2})$") String cnpj, @NotBlank String address, @NotBlank @Pattern(regexp = "^\\(?[0-9]{2}\\)?\\s?[0-9]{5}-?[0-9]{4}$") String telephone, User manager, Set<Pizza> pizzas) {
        this.name = name;
        this.cnpj = cnpj;
        this.address = address;
        this.telephone = telephone;
        this.manager = manager;
        this.pizzas = pizzas;
    }

    public EntityModel<Restaurant> toModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(RestaurantController.class).show(id)).withSelfRel(),
            linkTo(methodOn(RestaurantController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(RestaurantController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public EntityModel<Restaurant> toNoSelfModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(RestaurantController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(RestaurantController.class).index(Pageable.unpaged(), null)).withRel("list-all")
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Set<Pizza> getPizzas() {
        return pizzas;
    }

    @Override
    public String toString() {
        return "Restaurant [id=" + id + ", name=" + name + ", cnpj=" + cnpj + ", address=" + address + ", telephone=" + telephone + ", pizzas=" + pizzas + "]";
    }
}
