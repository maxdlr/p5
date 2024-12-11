package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SessionControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;
    private String jwtToken;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        baseUrl = "http://localhost:" + port + "/api/session";
        jwtToken = getJwtToken();
    }

    @AfterEach
    public void teardown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    private String getJwtToken() throws JsonProcessingException {
        User user = new User();
        user.setId(99L);
        user.setEmail("login@gmail.com");
        user.setPassword(passwordEncoder.encode("password1"));
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setCreatedAt(LocalDateTime.now());
        user.setAdmin(false);

        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@gmail.com");
        loginRequest.setPassword("password1");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/auth/login", loginRequest, String.class);

        assertEquals(200, loginResponse.getStatusCodeValue(), "Login failed");

        String responseBody = loginResponse.getBody();
        assert responseBody != null;

        return new ObjectMapper().readTree(responseBody).get("token").asText();
    }


    private HttpHeaders createHeadersWithBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }

    @Test
    public void testCreateSession_Success() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        SessionDto sessionDto = makeSessionDto(1, users, teacher);
        HttpEntity<SessionDto> entity = new HttpEntity<>(sessionDto, createHeadersWithBearerToken());
        ResponseEntity<SessionDto> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, SessionDto.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("name1", response.getBody().getName());
    }

    @Test
    public void testFindSessionById_Success() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        Session session = makeSession(1, users, teacher);
        sessionRepository.save(session);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithBearerToken());
        ResponseEntity<SessionDto> response = restTemplate.exchange(baseUrl + "/" + session.getId(), HttpMethod.GET, entity, SessionDto.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(session.getId(), response.getBody().getId());
    }

    @Test
    public void testFindSessionById_NotFound() {
        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithBearerToken());
        ResponseEntity<SessionDto> response = restTemplate.exchange(baseUrl + "/999", HttpMethod.GET, entity, SessionDto.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindAllSessions() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        Session session1 = makeSession(1, users, teacher);
        Session session2 = makeSession(2, users, teacher);

        sessionRepository.save(session1);
        sessionRepository.save(session2);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithBearerToken());
        ResponseEntity<SessionDto[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, SessionDto[].class);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(Objects.requireNonNull(response.getBody()).length >= 2);
    }

    @Test
    public void testUpdateSession_Success() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        Session session = makeSession(1, users, teacher);
        sessionRepository.save(session);

        SessionDto sessionDto = makeSessionDto(1, users, teacher);
        sessionDto.setName(session.getName() + "-updated");

        HttpEntity<SessionDto> entity = new HttpEntity<>(sessionDto, createHeadersWithBearerToken());
        restTemplate.exchange(baseUrl + "/" + session.getId(), HttpMethod.PUT, entity, SessionDto.class);
        ResponseEntity<SessionDto> response = restTemplate.exchange(baseUrl + "/" + session.getId(), HttpMethod.GET, entity, SessionDto.class);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(sessionDto.getName(), response.getBody().getName());
    }

    @Test
    public void testDeleteSession_Success() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        Session session = makeSession(1, users, teacher);
        sessionRepository.save(session);

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithBearerToken());
        restTemplate.exchange(baseUrl + "/" + session.getId(), HttpMethod.DELETE, entity, Void.class);

        ResponseEntity<SessionDto> response = restTemplate.exchange(baseUrl + "/" + session.getId(), HttpMethod.GET, entity, SessionDto.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteSession_NotFound() {
        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithBearerToken());
        ResponseEntity<?> response = restTemplate.exchange(baseUrl + "/999", HttpMethod.DELETE, entity, Void.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testParticipateInSession_Success() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        Session session = makeSession(1, users, teacher);
        sessionRepository.save(session);

        User participant = makeUser();
        userRepository.save(participant);
        String userId = participant.getId().toString();

        HttpEntity<String> httpEntity = new HttpEntity<>(createHeadersWithBearerToken());
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + session.getId() + "/participate/" + userId, HttpMethod.POST, httpEntity, String.class);

        assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<?> badRequestResponse =
                restTemplate
                        .exchange(
                                baseUrl + "/badRequest",
                                HttpMethod.POST,
                                httpEntity, String.class
                        );
        assertTrue(badRequestResponse.getStatusCodeValue() >= 400);

    }

    @Test
    public void testNoLongerParticipateInSession_Success() {
        Teacher teacher = makeTeacher();
        teacherRepository.save(teacher);

        List<User> users = makeUsers();
        userRepository.saveAll(users);

        Session session = makeSession(1, users, teacher);
        sessionRepository.save(session);

        String userId = users.get(0).getId().toString();
        HttpEntity<String> httpEntity = new HttpEntity<>(createHeadersWithBearerToken());
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + session.getId() + "/participate/" + userId,
                HttpMethod.DELETE,
                httpEntity,
                String.class
        );

        assertEquals(200, response.getStatusCodeValue());

        ResponseEntity<?> badRequestResponse =
                restTemplate
                        .exchange(
                                baseUrl + "/badRequest",
                                HttpMethod.POST,
                                httpEntity, String.class
                        );
        assertTrue(badRequestResponse.getStatusCodeValue() >= 400);
    }

    private List<User> makeUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setAdmin(false)
                    .setEmail(i + "@gmail.com")
                    .setCreatedAt(LocalDateTime.now())
                    .setPassword("password" + i)
                    .setLastName("lastName" + i)
                    .setFirstName("firstName" + i);
            users.add(user);
        }
        return users;
    }

    private User makeUser() {
        User user = new User();
        user.setAdmin(false)
                .setEmail("singleuser@gmail.com")
                .setCreatedAt(LocalDateTime.now())
                .setPassword("password1")
                .setLastName("lastName1")
                .setFirstName("firstName1");
        return user;
    }

    private Teacher makeTeacher() {
        Teacher teacher = new Teacher();
        teacher
                .setCreatedAt(LocalDateTime.now())
                .setFirstName("teacher")
                .setLastName("teacher");
        return teacher;
    }

    private Session makeSession(Integer id, List<User> users, Teacher teacher) {
        Session session = new Session();
        session
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setName("session" + id)
                .setDescription("description" + id)
                .setUsers(users)
                .setTeacher(teacher)
                .setDate(new Date());
        return session;
    }

    private SessionDto makeSessionDto(Integer id, List<User> users, Teacher teacher) {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId((long) id);
        sessionDto.setName("name" + id);
        sessionDto.setDescription("description" + id);
        sessionDto.setTeacher_id(teacher.getId());

        List<Long> userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
        sessionDto.setUsers(userIds);

        sessionDto.setDate(new Date());
        return sessionDto;
    }
}
