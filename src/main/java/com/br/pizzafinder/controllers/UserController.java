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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import com.br.pizzafinder.models.User;
import com.br.pizzafinder.models.UserLoginCredentials;
import com.br.pizzafinder.repositories.UserRepository;
import com.br.pizzafinder.services.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    UserRepository repo;

    @Autowired
    PagedResourcesAssembler<User> assembler;
    
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager autheManager;

    @Autowired
    TokenService tokenService;

    @GetMapping
    public PagedModel<EntityModel<User>> index(@PageableDefault(size = 20) Pageable pageable, @RequestParam(required = false) String query) {
        return assembler.toModel(query == null || query.equals("") ? repo.findAll(pageable) : repo.findByEmail(pageable, query));
    }

    @GetMapping("{id}")
    public EntityModel<User> show(@PathVariable Long id) {
        //TODO: Criar um record que deixa o output de User mais legível

        return repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!")
        ).toNoSelfModel();
    }

    @PostMapping("/create")
    public ResponseEntity<EntityModel<User>> create(@RequestBody @Valid User user) {
        user.setPassword(encoder.encode(user.getPassword()));

        repo.save(user);

        return ResponseEntity.created(
            user.toModel().getRequiredLink("self").toUri()
        ).body(user.toModel());
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginCredentials credentials) {
        autheManager.authenticate(credentials.toAuthentication());

        String token = tokenService.generateToken(credentials);

        return ResponseEntity.ok(token);
    }

    @PutMapping("{id}")
    public ResponseEntity<EntityModel<User>> update(@PathVariable Long id, @RequestBody @Valid User user) {
        user.setPassword(encoder.encode(user.getPassword()));

        User u = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!")
        );

        BeanUtils.copyProperties(user, u, "id");

        repo.save(u);

        return ResponseEntity.ok(u.toModel());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> destroy(@PathVariable Long id) {
        User user = repo.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!")
        );

        repo.delete(user);

        return ResponseEntity.noContent().build();
    }
}
