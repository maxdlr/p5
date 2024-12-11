package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TeacherControllerIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TeacherRepository teacherRepository;

    private String baseUrl;
    private String token;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        baseUrl = "http://localhost:" + port + "/api/teacher";
        token = getJwtToken();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    private String getJwtToken() throws JsonProcessingException {
        User user = makeUser(1);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword("password1");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:"+port+"/api/auth/login", loginRequest, String.class);
        assertEquals(200, loginResponse.getStatusCodeValue());

        String responseBody = loginResponse.getBody();
        assert responseBody != null;

        return new ObjectMapper().readTree(responseBody).get("token").asText();
    }

    private HttpHeaders createHeadersWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

  @Test
  public void testFindTeacherById() {
    Teacher teacher = makeTeacher(55);
    teacherRepository.save(teacher);

    HttpEntity<Teacher> request = new HttpEntity<>(createHeadersWithToken());

    ResponseEntity<Teacher> response =
        restTemplate.exchange(
            baseUrl + "/" + teacher.getId(), HttpMethod.GET, request, Teacher.class);

    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void testFindAllTeachers() {
        for(int i = 12; i <= 21; i++) {
            Teacher teacher = makeTeacher(i);
            teacherRepository.save(teacher);
        }

        HttpEntity<List<Teacher>> request = new HttpEntity<>(null, createHeadersWithToken());
        ResponseEntity<?> response = restTemplate.exchange(baseUrl, HttpMethod.GET, request, List.class);

        assertEquals(200, response.getStatusCodeValue());

        List<Teacher> responseBody = (List<Teacher>) response.getBody();
      assert responseBody != null;
      assertEquals(10, responseBody.size());
  }

  private Teacher makeTeacher(int id) {
        Teacher teacher = new Teacher();
        teacher
                .setFirstName("firstname" + id)
                .setLastName("lastname" + id)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        return teacher;
    }

    private User makeUser(Integer id) {
        User user = new User();
        user.setId((long) id)
                .setFirstName("firstname" + id)
                .setLastName("lastname" + id)
                .setEmail("mail@mail.com" + id)
                .setPassword(passwordEncoder.encode("password" + id))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        return user;

    }
}
