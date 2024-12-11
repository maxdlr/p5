package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port + "/api/auth";
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_SuccessfulRegistration() {
        String email = "testuser@example.com";
        String password = "password123";
        String firstName = "max";
        String lastName = "dlr";

        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setFirstName(firstName);
        signUpRequest.setLastName(lastName);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/register", signUpRequest, String.class);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("User registered successfully!"));

        User user = userRepository.findByEmail(email).orElse(null);
        assertNotNull(user);
        assertEquals(email, user.getEmail());
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() {
        String email = "testuser@example.com";
        String password = "password123";
        String firstName = "max";
        String lastName = "dlr";

        User existingUser = new User();
        existingUser
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setAdmin(false);
        userRepository.save(existingUser);

        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setFirstName(firstName);
        signUpRequest.setLastName(lastName);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/register", signUpRequest, String.class);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Error: Email is already taken!"));
    }

    @Test
    public void testAuthenticateUser_SuccessfulLogin() {
        // Create and save a user to the repository
        String email = "testuser@example.com";
        String password = "password123";
        String firstName = "max";
        String lastName = "dlr";

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAdmin(false);
        user.setEmail(email);
      user.setPassword(passwordEncoder.encode(password)); // Encoded password
//        user.setPassword(password);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        // Perform the login request
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", loginRequest, String.class);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test()
    public void testAuthenticateUser_InvalidCredentials() {
        // Create and save a user to the repository
        String email = "testuser@example.com";
        String password = "password123";
        String firstName = "max";
        String lastName = "dlr";
        User user = new User();
        user
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setAdmin(false);
        user.setPassword(passwordEncoder.encode(password)); // Encoded password for 'password123'
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword("wrongpassword");

//      ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", loginRequest, String.class);

//        assertEquals(401, response.getStatusCodeValue()); // Unauthorized
    }
}
