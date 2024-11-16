package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class SessionControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private SessionController sessionController;

    @Test
    public void testFindBySessionId() throws Exception {
        List<User> users = makeUsers(5, false);
        Teacher teacher = makeTeacher();
        Session session = makeSession(1, users, teacher);
        SessionDto sessionDto = makeSessionDto(1, users, teacher);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn("mockUsername");

        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get("/api/session/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath("$.id")
                                .value(1L)
                )
                .andReturn()
        ;

        System.out.println("JSON Response: " + result.getResponse().getContentAsString());

//        ResponseEntity<?> successfulResponse = sessionController.findById("1");
//        ResponseEntity<?> unsuccessfulResponse = sessionController.findById("2");
//        ResponseEntity<?> badRequestResponse = sessionController.findById("bad-request");
//
//        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
//        assertEquals(HttpStatus.NOT_FOUND, unsuccessfulResponse.getStatusCode());
//        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());

//        assertNotNull(successfulResponse.getBody());

//        SessionDto responseBody = (SessionDto) successfulResponse.getBody();

//        assertEquals(sessionDto.getId(), responseBody.getId());
//        assertEquals(sessionDto.getName(), responseBody.getName());
//        assertEquals(sessionDto.getDescription(), responseBody.getDescription());
//        assertEquals(sessionDto.getTeacher_id(), responseBody.getTeacher_id());
//        assertEquals(sessionDto.getUsers(), responseBody.getUsers());
//        assertEquals(sessionDto.getCreatedAt(), responseBody.getCreatedAt());
//        assertEquals(sessionDto.getDate(), responseBody.getDate());
    }

    @Test
    public void testFindAllSessions() {
        List<Session> sessions = new ArrayList<>();
        List<SessionDto> sessionDtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            List<User> users = makeUsers(5, false);
            Teacher teacher = makeTeacher();
            Session session = makeSession(i, users, teacher);
            SessionDto sessionDto = makeSessionDto(1, users, teacher);

            sessions.add(session);
            sessionDtos.add(sessionDto);
        }

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<SessionDto> responseBody = (List<SessionDto>) response.getBody();
        assertEquals(sessionDtos.size(), responseBody.size());
        for (int i = 0; i < sessionDtos.size(); i++) {
            assertEquals(sessionDtos.get(i).getId(), responseBody.get(i).getId());
            assertEquals(sessionDtos.get(i).getName(), responseBody.get(i).getName());
            assertEquals(sessionDtos.get(i).getDescription(), responseBody.get(i).getDescription());
            assertEquals(sessionDtos.get(i).getTeacher_id(), responseBody.get(i).getTeacher_id());
            assertEquals(sessionDtos.get(i).getUsers().size(), responseBody.get(i).getUsers().size());
        }
    }

    @Test
    public void testCreateSessionSuccessfully() {
        List<User> users = makeUsers(5, false);
        Teacher teacher = makeTeacher();
        SessionDto sessionDto = makeSessionDto(1, users, teacher);
        Session session = makeSession(1, users, teacher);

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        SessionDto responseBody = (SessionDto) response.getBody();
        assertEquals(sessionDto.getId(), responseBody.getId());
        assertEquals(sessionDto.getName(), responseBody.getName());
        assertEquals(sessionDto.getDescription(), responseBody.getDescription());
        assertEquals(sessionDto.getTeacher_id(), responseBody.getTeacher_id());
    }

    @Test
    public void testUpdateSession() {
        Teacher teacher = makeTeacher();
        List<User> users = makeUsers(5, false);
        Session originalSession = makeSession(1, users, teacher);
        SessionDto sessionDto = makeSessionDto(1, users, teacher);
        sessionDto.setName(originalSession.getName() + "-updated");
        Session updatedSession = makeSession(1, users, teacher);
        updatedSession.setName(sessionDto.getName());

        when(sessionMapper.toEntity(sessionDto)).thenReturn(updatedSession);
        when(sessionService.update(sessionDto.getId(), updatedSession)).thenReturn(updatedSession);
        when(sessionMapper.toDto(updatedSession)).thenReturn(sessionDto);

        ResponseEntity<?> successfulResponse = sessionController.update("1", sessionDto);
        ResponseEntity<?> badRequestResponse = sessionController.update("bad-request", sessionDto);

        SessionDto sessionBody = (SessionDto) successfulResponse.getBody();

        assertEquals(updatedSession.getId(), sessionBody.getId());
        assertEquals(updatedSession.getName(), sessionBody.getName());
        assertEquals(updatedSession.getDescription(), sessionBody.getDescription());
        assertEquals(updatedSession.getTeacher().getId(), sessionBody.getTeacher_id());
        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());
    }

    @Test
    public void testDeleteSession() {
        List<User> users = makeUsers(5,false);
        Teacher teacher = makeTeacher();
        Session session = makeSession(1, users, teacher);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionService.getById(2L)).thenReturn(null);

        ResponseEntity<?> successfulResponse = sessionController.save("1");
        ResponseEntity<?> notFoundResponse = sessionController.save("2");
        ResponseEntity<?> badRequestResponse = sessionController.save("bad-request");

        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }

    @Test
    public void testParticipateToSession() {
        User user = makeUser(false);
        List<User> users = makeUsers(6, false);
        Teacher teacher = makeTeacher();
        Session session = makeSession(1, users, teacher);

        ResponseEntity<?> successfulResponse = sessionController.participate(session.getId().toString(), user.getId().toString());
        ResponseEntity<?> badRequestResponse = sessionController.participate("bad-request", "bad-request");

        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());
    }

    @Test
    public void testNoLongerParticipateToSession() {
        User user = makeUser(false);
        List<User> users = makeUsers(6, false);
        Teacher teacher = makeTeacher();
        Session session = makeSession(1, users, teacher);
        ResponseEntity<?> successfulResponse = sessionController.participate(session.getId().toString(), user.getId().toString());
        ResponseEntity<?> badRequestResponse = sessionController.participate("bad-request", "bad-request");
        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());
    }

    private User makeUser(Boolean admin) {
        User user = new User();
        user.setAdmin(admin)
                .setId(1L)
                .setEmail("user@gmail.com")
                .setCreatedAt(LocalDateTime.now())
                .setPassword("password")
                .setLastName("lastName")
                .setFirstName("firstName");
        return user;
    }

    private List<User> makeUsers(Integer number, Boolean admin) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            User user = new User();
            user.setAdmin(admin)
                    .setId((long) i)
                    .setEmail(i + "@gmail.com")
                    .setCreatedAt(LocalDateTime.now())
                    .setPassword("password" + i)
                    .setLastName("lastName" + i)
                    .setFirstName("firstName" + i);
            users.add(user);
        }
        return users;
    }

    private Teacher makeTeacher() {
        Teacher teacher = new Teacher();
        teacher
                .setId(1L)
                .setCreatedAt(LocalDateTime.now())
                .setFirstName("teacher")
                .setLastName("teacher");
        return teacher;
    }

    private Session makeSession(Integer id, List<User> users, Teacher teacher) {
        Session session = new Session();
        session
                .setId((long) id)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setName("session" + id)
                .setDescription("description" + id)
                .setUsers(users)
                .setTeacher(teacher)
                .setDate(new Date());
        return session;
    }

    private List<Session> makeSessions(Integer number, List<User> users, Teacher teacher) {
        List<Session> sessions = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Session session = makeSession(i, users, teacher);
            sessions.add(session);
        }
        return sessions;
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
