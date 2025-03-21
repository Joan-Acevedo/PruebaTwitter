package com.example.twitter.services;

import com.example.twitter.model.User;
import com.example.twitter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser() {
        // Arrange
        User userToRegister = new User();
        userToRegister.setUsername("testuser");
        userToRegister.setPassword("password123");

        User savedUser = new User();
        savedUser.setId("user123");
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(userToRegister);

        // Assert
        assertNotNull(result);
        assertEquals("user123", result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserAlreadyExists() {
        // Arrange
        User userToRegister = new User();
        userToRegister.setUsername("existinguser");
        userToRegister.setPassword("password123");

        User existingUser = new User();
        existingUser.setId("user123");
        existingUser.setUsername("existinguser");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(userToRegister);
        });

        assertEquals("El usuario ya existe", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(
                new User("1","user1", "User 1"),
                new User("2","user2", "User 2")
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void existsByUsername() {
        // Arrange
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        // Act
        boolean result = userService.existsByUsername(username);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void notExistsByUsername() {
        // Arrange
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.existsByUsername(username);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).findByUsername(username);
    }
}
