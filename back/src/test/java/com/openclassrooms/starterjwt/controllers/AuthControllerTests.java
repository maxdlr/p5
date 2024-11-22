package com.openclassrooms.starterjwt.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        AuthController sessionController = new AuthController(
                authenticationManager,
                passwordEncoder,
                jwtUtils,
                userRepository
        );

        mvc = MockMvcBuilders
                .standaloneSetup(sessionController)
                .build();
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        String email = "yoga@studio.com";
        String password = "test!1234";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "yoga@studio.com",
                "Admin",
                "Admin",
                true,
                "test!1234"
        );

        User user = new User(
                userDetails.getUsername(),
                userDetails.getLastName(),
                userDetails.getFirstName(),
                userDetails.getPassword(),
                true
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockJwtToken");

        String successPayload = new ObjectMapper().writeValueAsString(loginRequest);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .content(successPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDetails.getId()))
                .andExpect(jsonPath("$.token").value("mockJwtToken"));

        String failurePayload = new ObjectMapper().writeValueAsString(new LoginRequest());

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .content(failurePayload)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterUser() throws Exception {
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

        User user = new User();
        user
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setAdmin(false);

        String payload = new ObjectMapper().writeValueAsString(signUpRequest);

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        signUpRequest.setEmail(null);
        payload = new ObjectMapper().writeValueAsString(signUpRequest);

        mvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }
}
