package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTests {

    private MockMvc mvc;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @BeforeEach
    public void setUp() {
        SessionController sessionController = new SessionController(sessionService, sessionMapper);

        mvc = MockMvcBuilders
                .standaloneSetup(sessionController)
                .build();
    }

    @Test
    public void testFindBySessionId() throws Exception {
        List<User> users = makeUsers(5, false);
        Teacher teacher = makeTeacher();
        Session session = makeSession(1, users, teacher);
        SessionDto sessionDto = makeSessionDto(1, users, teacher);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mvc.perform(MockMvcRequestBuilders
                        .get("/api/session/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath("$")
                                .value(sessionDto)
                );

        mvc.perform(MockMvcRequestBuilders
                        .get("/api/session/{id}", "bad-request")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath("$")
                                .doesNotExist()
                );

    }

    @Test
    public void testFindAllSessions() throws Exception {
        List<Session> sessions = new ArrayList<>();
        List<SessionDto> sessionDtos = new ArrayList<>();

        SessionDto sessionDto = null;
        for (int i = 0; i < 10; i++) {
            List<User> users = makeUsers(5, false);
            Teacher teacher = makeTeacher();
            Session session = makeSession(i, users, teacher);
            sessionDto = makeSessionDto(i, users, teacher);

            sessions.add(session);
            sessionDtos.add(sessionDto);
        }

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        mvc.perform(MockMvcRequestBuilders
                        .get("/api/session")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(sessionDtos.get(0).getId().toString())
                ).andExpect( MockMvcResultMatchers.jsonPath("$[" + (sessionDtos.size() - 1) + "].id")
                        .value(sessionDtos.get(sessionDtos.size() - 1).getId().toString())
                );
    }

    @Test
    public void testCreateSessionSuccessfully() throws Exception {
        List<User> users = makeUsers(5, false);
        Teacher teacher = makeTeacher();

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("session1");
        sessionDto.setDescription("description1");
        sessionDto.setTeacher_id(1L);
        sessionDto.setDate(new Date());

        Session session = makeSession(1, users, teacher);

        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        String jsonPayload = new ObjectMapper().writeValueAsString(sessionDto);

        mvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(sessionDto))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(session.getName()))
                .andReturn()
        ;
        mvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .content(new ObjectMapper().writeValueAsString(new SessionDto()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
                .andReturn()
        ;
    }

    @Test
    public void testUpdateSession() throws Exception {
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

        String jsonPayload = new ObjectMapper().writeValueAsString(sessionDto);

        mvc.perform(MockMvcRequestBuilders.put("/api/session/{id}", 1L)
                .content(jsonPayload)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(sessionDto))
        ;

        mvc.perform(MockMvcRequestBuilders.put("/api/session/{id}", "bad-request")
                        .content(jsonPayload)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
        ;
    }

    @Test
    public void testDeleteSession() throws Exception {
        List<User> users = makeUsers(5,false);
        Teacher teacher = makeTeacher();
        Session session = makeSession(1, users, teacher);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionService.getById(2L)).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", 2L)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());

        mvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", "bad-request")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testParticipateToSession() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/session/{id}/participate/{userId}", 1L, 1L)
                        .param("id", "1")
                        .param("userId", "1")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void testNoLongerParticipateToSession() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}/participate/{userId}", 1L, 1L)
                .param("id", "1")
                .param("userId", "1")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
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
