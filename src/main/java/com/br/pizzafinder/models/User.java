package com.br.pizzafinder.models;

import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.br.pizzafinder.controllers.UserController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//TODO: Adicionar roles

@Entity
@Table(
    name = "user_tb",
    uniqueConstraints = @UniqueConstraint(
        name = "user_email_uk",
        columnNames = "user_email"
    )
)
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(
        generator = "user_seq",
        strategy = GenerationType.SEQUENCE
    )
    @SequenceGenerator(
        name = "user_seq",
        sequenceName = "user_seq",
        allocationSize = 1
    )
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[A-Z][a-zA-Z '.-]*[A-Za-z][^-][^ ]$")
    @Column(name = "user_name")
    private String name;

    @Email
    @Column(name = "user_email")
    private String email;

    @NotBlank
    @Size(min = 8)
    @Column(name = "user_password")
    private String password;

    public User() {}

    public User(@NotBlank @Pattern(regexp = "^\b([A-ZÀ-ÿ][-,a-z. ']+ +[A-ZÀ-ÿ][-,a-z. ']*)+$") String name, @Email String email, @NotBlank @Size(min = 8) String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public EntityModel<User> toModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(UserController.class).show(id)).withSelfRel(),
            linkTo(methodOn(UserController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(UserController.class).index(Pageable.unpaged(), null)).withRel("list-all")
        );
    }

    public EntityModel<User> toNoSelfModel() {
        return EntityModel.of(
            this,
            linkTo(methodOn(UserController.class).destroy(id)).withRel("destroy"),
            linkTo(methodOn(UserController.class).index(Pageable.unpaged(), null)).withRel("list-all")
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER_ROLE"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + "]";
    }
}   
