package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl;
    private String jwtToken;

    private User authenticatedUser;

    @BeforeEach()
    public void setUp() throws JsonProcessingException {
        baseUrl = "http://localhost:" + port + "/api/user";
        jwtToken = getJwtToken();
    }

    private String getJwtToken() throws JsonProcessingException {
        authenticatedUser = makeUser(1);
        userRepository.save(authenticatedUser);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(authenticatedUser.getEmail());
        loginRequest.setPassword("password1");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:"+port+"/api/auth/login", loginRequest, String.class);
        assertEquals(200, loginResponse.getStatusCodeValue());

        String responseBody = loginResponse.getBody();
        assert responseBody != null;

        return new ObjectMapper().readTree(responseBody).get("token").asText();
    }

    private HttpHeaders createHeadersWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindUserById() {
        User user = makeUser(65);
        userRepository.save(user);

        UserDto userDto = makeUserDto(65);

        HttpEntity<UserDto> httpEntity = new HttpEntity<>(createHeadersWithToken());

        ResponseEntity<UserDto> response = restTemplate
                .exchange(baseUrl + "/" + user.getId(), HttpMethod.GET,httpEntity, UserDto.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getId(), Objects.requireNonNull(response.getBody()).getId());
        assertEquals(userDto.getEmail(), Objects.requireNonNull(response.getBody()).getEmail());

        ResponseEntity<UserDto> notFoundResponse = restTemplate
                .exchange(baseUrl + "/99", HttpMethod.GET,httpEntity, UserDto.class);

        assertEquals(404, notFoundResponse.getStatusCodeValue());

        ResponseEntity<UserDto> badRequestResponse = restTemplate
                .exchange(baseUrl + "/bad-request", HttpMethod.GET,httpEntity, UserDto.class);

        assertEquals(400, badRequestResponse.getStatusCodeValue());
    }

    @Test
    public void testDeleteUser() {
        HttpEntity<UserDto> httpEntity = new HttpEntity<>(createHeadersWithToken());

        ResponseEntity<?> response = restTemplate
                .exchange(baseUrl + "/" + authenticatedUser.getId(), HttpMethod.DELETE, httpEntity, UserDto.class);

        assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<?> badRequestResponse = restTemplate
                .exchange(baseUrl + "/" + authenticatedUser.getId() + 1, HttpMethod.DELETE, httpEntity, UserDto.class);

        assertTrue(badRequestResponse.getStatusCodeValue() > 399);
    }

    private UserDto makeUserDto(int id) {
        UserDto userDto = new UserDto();
        userDto.setEmail("email@email.com" + id);
        userDto.setFirstName("firstName" + id);
        userDto.setLastName("lastName" + id);
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());
        userDto.setAdmin(false);
        return userDto;
    }

    private User makeUser(Integer id) {
        User user = new User();
        user
                .setFirstName("firstname" + id)
                .setLastName("lastname" + id)
                .setEmail("email@email.com" + id)
                .setPassword(passwordEncoder.encode("password" + id))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        return user;

    }
}
