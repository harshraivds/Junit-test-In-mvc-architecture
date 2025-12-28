package com.mvc_unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mvc_unit.entity.User;
import com.mvc_unit.repository.UserRepository;
import com.mvc_unit.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@Mock
	private UserRepository repository;
	
	
	@InjectMocks
	private UserService service;
	
	@Test
    void getUserName_found() {
        when(repository.findById(1)).thenReturn(Optional.of(new User(1, "Alice")));

        String result = service.getUserName(1);

        assertEquals("Alice", result);
        verify(repository, times(1)).findById(1);
    }

    @Test
    void getUserName_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        String result = service.getUserName(99);

        assertEquals("Unknown User", result);
        verify(repository, times(1)).findById(99);
    }

    @Test
    void createUser_saves() {
        User input = new User(10, "Bob");
        when(repository.save(any(User.class))).thenReturn(input);

        User saved = service.createUser(input);

        assertEquals("Bob", saved.getName());
        verify(repository).save(input);
    }

    @Test
    void updateUser_found_updates() {
        when(repository.findById(5)).thenReturn(Optional.of(new User(5, "Old")));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User body = new User(0, "New"); // id is overridden by service
        User updated = service.updateUser(5, body).orElseThrow();

        assertEquals(5, updated.getId());
        assertEquals("New", updated.getName());
        verify(repository).findById(5);
        verify(repository).save(any(User.class));
    }

    @Test
    void updateUser_notFound() {
        when(repository.findById(6)).thenReturn(Optional.empty());

        var result = service.updateUser(6, new User(6, "X"));

        assertEquals(false, result.isPresent());
        verify(repository).findById(6);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteUser_found_returnsTrue() {
        when(repository.findById(7)).thenReturn(Optional.of(new User(7, "Y")));

        boolean deleted = service.deleteUser(7);

        assertEquals(true, deleted);
        verify(repository).findById(7);
        verify(repository).deleteById(7);
    }

    @Test
    void deleteUser_notFound_returnsFalse() {
        when(repository.findById(8)).thenReturn(Optional.empty());

        boolean deleted = service.deleteUser(8);

        assertEquals(false, deleted);
        verify(repository).findById(8);
        verify(repository, never()).deleteById(anyInt());
    }
    @Test
    void testFindAllUsers() {
        // given
        List<User> users = Arrays.asList(
            new User(1, "Alice"),
            new User(2, "Bob")
        );

        when(repository.findAll()).thenReturn(users);

        // when
        List<User> result = service.findAllUsers();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
        verify(repository, times(1)).findAll();
    }
}
