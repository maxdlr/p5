package com.openclassrooms.starterjwt.controllers;

import static org.junit.jupiter.api.Assertions.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthControllerTests {

    @Mock
    LoginRequest loginRequest;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testAuthenticateUserSuccessfully() {
        String email = "yoga@studio.com";
        String password = "test!1234";

        when(loginRequest.getEmail()).thenReturn(email);
        when(loginRequest.getPassword()).thenReturn(password);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "yoga@studio.com",
                "Admin",
                "Admin",
                true,
                "test!1234"
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockJwtToken");

        User user = new User(
                userDetails.getUsername(),
                userDetails.getLastName(),
                userDetails.getFirstName(),
                userDetails.getPassword(),
                true
        );

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        var response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertEquals(userDetails.getId(), jwtResponse.getId());
        assertEquals(userDetails.getUsername(), jwtResponse.getUsername());
        assertEquals("mockJwtToken", jwtResponse.getToken());
    }

    @Test
    public void testAuthenticateUserUnsuccessfully() {
        String email = "yoga@studio.com";
        String password = "test!1234";

        when(loginRequest.getEmail()).thenReturn(email);
        when(loginRequest.getPassword()).thenReturn(password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Invalid Credentials") {});

        assertThrows(AuthenticationException.class, () -> authController.authenticateUser(loginRequest));
    }

    @Test
    public void testRegisterUserSuccessfully() {
        String email = "yoga@studio.com";
        String password = "test!1234";
        String firstName = "max";
        String lastName = "dlr";

        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setFirstName(firstName);
        signUpRequest.setLastName(lastName);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encoded" + password);

        var response = authController.registerUser(signUpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testRegisterUserUnsuccessfully() {
        String email = "yoga@studio.com";
        String password = "test!1234";
        String firstName = "max";
        String lastName = "dlr";

        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setFirstName(firstName);
        signUpRequest.setLastName(lastName);

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(passwordEncoder.encode(password)).thenReturn("encoded" + password);

        var response = authController.registerUser(signUpRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
