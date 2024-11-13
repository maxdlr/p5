package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTests {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    @Test
    public void testFindBySessionId() {
        Session session = new Session();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setAdmin(false)
                    .setId((long) i)
                    .setEmail(i + "@gmail.com")
                    .setCreatedAt(LocalDateTime.now())
                    .setPassword("password")
                    .setLastName("lastName")
                    .setFirstName("firstName");
            users.add(user);
        }

        Teacher teacher = new Teacher();
        teacher.setId(1L)
                .setFirstName("firstName")
                .setLastName("lastName")
                .setCreatedAt(LocalDateTime.now());

        session
                .setId(1L)
                .setName("name")
                .setDescription("description")
                .setUsers(users)
                .setTeacher(teacher)
                .setCreatedAt(LocalDateTime.now())
                .setDate(new Date());

        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("name");
        sessionDto.setDescription("description");
        sessionDto.setTeacher_id(teacher.getId());
        sessionDto.setUsers(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)));
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setDate(new Date());

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        ResponseEntity<?> successfulResponse = sessionController.findById("1");
        ResponseEntity<?> unsuccessfulResponse = sessionController.findById("2");
        ResponseEntity<?> badRequestResponse = sessionController.findById("bad-request");

        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, unsuccessfulResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());

        assertNotNull(successfulResponse.getBody());

        SessionDto responseBody = (SessionDto) successfulResponse.getBody();

        assertEquals(sessionDto.getId(), responseBody.getId());
        assertEquals(sessionDto.getName(), responseBody.getName());
        assertEquals(sessionDto.getDescription(), responseBody.getDescription());
        assertEquals(sessionDto.getTeacher_id(), responseBody.getTeacher_id());
        assertEquals(sessionDto.getUsers(), responseBody.getUsers());
        assertEquals(sessionDto.getCreatedAt(), responseBody.getCreatedAt());
        assertEquals(sessionDto.getDate(), responseBody.getDate());
    }

    @Test
    public void testFindAllSessions() {
        List<Session> sessions = new ArrayList<>();
        List<SessionDto> sessionDtos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Session session = new Session();

            List<User> users = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                User user = new User();
                user.setAdmin(i % 3 == 0)
                        .setId((long) j)
                        .setEmail(j + "@gmail.com")
                        .setCreatedAt(LocalDateTime.now())
                        .setPassword("password")
                        .setLastName("lastName")
                        .setFirstName("firstName");
                users.add(user);
            }

            Teacher teacher = new Teacher();
            teacher.setId(1L)
                    .setFirstName("firstName")
                    .setLastName("lastName")
                    .setCreatedAt(LocalDateTime.now());

            session.setId((long) i)
                    .setName("name" + i)
                    .setDescription("description" + i)
                    .setUsers(users)
                    .setTeacher(teacher)
                    .setCreatedAt(LocalDateTime.now());
            sessions.add(session);

            SessionDto sessionDto = new SessionDto();
            sessionDto.setId((long) i);
            sessionDto.setName("name" + i);
            sessionDto.setDescription("description" + i);
            sessionDto.setTeacher_id(teacher.getId());
            sessionDto.setUsers(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)));
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
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setUpdatedAt(LocalDateTime.now());
        sessionDto.setName("test");
        sessionDto.setDescription("description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setDate(new Date());

        Session session = new Session();

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
        Teacher teacher = new Teacher();
        teacher
                .setId(1L)
                .setCreatedAt(LocalDateTime.now())
                .setFirstName("firstName")
                .setLastName("lastName");

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setAdmin(i % 3 == 0)
                    .setId((long) i)
                    .setEmail(i + "@gmail.com")
                    .setCreatedAt(LocalDateTime.now())
                    .setPassword("password")
                    .setLastName("lastName")
                    .setFirstName("firstName");
            users.add(user);
        }

        Session originalSession = new Session();
        originalSession
                .setId(1L)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setName("test")
                .setDescription("description")
                .setUsers(users)
                .setTeacher(teacher)
                .setDate(new Date());



        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("updated-test");
        sessionDto.setDescription("updated-description");
        sessionDto.setTeacher_id(1L);
        sessionDto.setDate(new Date());

        Session updatedSession = new Session();
        updatedSession
                .setId(1L)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setName(sessionDto.getName())
                .setDescription(sessionDto.getDescription())
                .setUsers(users)
                .setTeacher(teacher)
                .setDate(new Date());
        when(sessionMapper.toEntity(sessionDto)).thenReturn(updatedSession);

        ResponseEntity<?> successfulResponse = sessionController.update("1", sessionDto);
        ResponseEntity<?> badRequestResponse = sessionController.update("bad-request", sessionDto);

        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, badRequestResponse.getStatusCode());
    }

    //todo: delete, participate, noLongerParticipate
}
