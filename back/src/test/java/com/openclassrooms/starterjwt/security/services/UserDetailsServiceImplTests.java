package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository;


    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }


    @Test
    public void testLoadUserByUsername() {
        User user = new User();
        user
            .setId(1L)
            .setFirstName("John")
            .setLastName("Doe")
            .setEmail("john@doe.com")
            .setPassword("password")
            .setCreatedAt(LocalDateTime.now())
            .setUpdatedAt(LocalDateTime.now());

    UserDetails userDetails = new UserDetailsImpl(
            1L,
            "john@doe.com",
            "firstname",
            "lastname",
            false,
            "password"
    );

        when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(user));

        UserDetails testUserDetails = userDetailsService.loadUserByUsername("john@doe.com");

        assertEquals(userDetails, testUserDetails);
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("john@doe.com"));
    }
}
