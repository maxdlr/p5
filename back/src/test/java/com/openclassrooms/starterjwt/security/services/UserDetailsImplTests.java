package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsImplTests {

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new UserDetailsImpl(
                1L,
                "username",
                "firstname",
                "lastname",
                false,
                "password"
        );
    }

    @Test
    public void testIsAccountNonExpired() {
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
        assertEquals(new HashSet<GrantedAuthority>(), userDetails.getAuthorities());

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(
                1L,
                "username",
                "firstname",
                "lastname",
                false,
                "password"
        );

        assertTrue(userDetails.equals(userDetails));
        assertTrue(userDetails.equals(userDetailsImpl));
        assertFalse(userDetails.equals(new User()));
        assertFalse(userDetails.equals(null));
    }
}
