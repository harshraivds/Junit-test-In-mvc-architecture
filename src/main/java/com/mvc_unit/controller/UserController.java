package com.mvc_unit.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvc_unit.entity.User;
import com.mvc_unit.service.UserService;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) { // constructor-based DI
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUserName(@PathVariable int id) {
        String name = service.getUserName(id);
        if ("Unknown User".equals(name)) {
            return ResponseEntity.notFound().build();      // 404
        }
        return ResponseEntity.ok(name);                    // 200
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = service.createUser(user);
        return ResponseEntity.status(201).body(created);   // 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
        Optional<User> updated = service.updateUser(id, user);
        return updated.map(ResponseEntity::ok)              // 200
                      .orElseGet(() -> ResponseEntity.notFound().build()); // 404
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        boolean deleted = service.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() // 204
                       : ResponseEntity.notFound().build(); // 404
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = service.findAllUsers();

        if (users.isEmpty()) {
            // Return HTTP 204 No Content
            return ResponseEntity.noContent().build();
        }

        // Return HTTP 200 OK with the list
        return ResponseEntity.ok(users);
    }
}

