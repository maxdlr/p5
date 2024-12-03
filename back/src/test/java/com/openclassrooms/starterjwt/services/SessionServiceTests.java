package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class SessionServiceTests {
    @Mock
    SessionRepository sessionRepository;

    @Mock
    UserRepository userRepository;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(sessionRepository, userRepository);
    }

    @Test
    public void testCreateSession() {
        Session session = new Session();
        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user.setId(1L);
            user.setEmail("john.doe@gmail.com");
            user.setAdmin(false);
            user.setPassword("password");
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setCreatedAt(LocalDateTime.now());
            users.add(user);
        }

        Teacher teacher = new Teacher();
        teacher.setId(1L).setFirstName("John").setLastName("Doe").setCreatedAt(LocalDateTime.now());

        session.setId(1L).setDate(new Date()).setTeacher(teacher).setUsers(users).setName("name").setDescription("description").setCreatedAt(LocalDateTime.now());

        when(sessionRepository.save(session)).thenReturn(session);

        Session createdSession = sessionService.create(session);

        assertEquals(session.getId(), createdSession.getId());
        assertEquals(session.getDate(), createdSession.getDate());
        assertEquals(session.getTeacher(), createdSession.getTeacher());
        assertEquals(session.getUsers(), createdSession.getUsers());
        assertEquals(session.getName(), createdSession.getName());
        assertEquals(session.getDescription(), createdSession.getDescription());
        assertEquals(session.getCreatedAt(), createdSession.getCreatedAt());
        assertEquals(session.getUpdatedAt(), createdSession.getUpdatedAt());
    }

    @Test
    public void testFindAllSessions() {
        List<Session> sessions = new ArrayList<>();
        Teacher teacher = new Teacher();
        teacher.setId(1L).setFirstName("John").setLastName("Doe").setCreatedAt(LocalDateTime.now()).setUpdatedAt(LocalDateTime.now());

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user
                    .setId((long) i)
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setEmail("john.doe@gmail.com")
                    .setAdmin(false)
                    .setPassword("password");
        }

        for(int i = 0; i < 10; i++) {
            Session session = new Session();
            session
                    .setId(1L)
                    .setDate(new Date())
                    .setTeacher(teacher)
                    .setUsers(users)
                    .setName("name")
                    .setDescription("description")
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now());
            sessions.add(session);
        }

        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> findAllSessions = sessionService.findAll();

        assertEquals(sessions.size(), findAllSessions.size());
        assertEquals(sessions.get(0).getId(), findAllSessions.get(0).getId());
        assertEquals(sessions.get(sessions.size() - 1).getId(), findAllSessions.get(findAllSessions.size() - 1).getId());
    }

    @Test
    public void testGetSessionById() {
        Teacher teacher = new Teacher();
        teacher.setId(1L).setFirstName("John").setLastName("Doe").setCreatedAt(LocalDateTime.now()).setUpdatedAt(LocalDateTime.now());

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user
                    .setId((long) i)
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setEmail("john.doe@gmail.com")
                    .setAdmin(false)
                    .setPassword("password");
        }
        Session session = new Session();
        session
                .setId(1L)
                .setDate(new Date())
                .setTeacher(teacher)
                .setUsers(users)
                .setName("name")
                .setDescription("description")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session foundSession = sessionService.getById(1L);

        assertEquals(session.getId(), foundSession.getId());
        assertEquals(session.getDate(), foundSession.getDate());
        assertEquals(session.getTeacher(), foundSession.getTeacher());
        assertEquals(session.getUsers(), foundSession.getUsers());
        assertEquals(session.getName(), foundSession.getName());
        assertEquals(session.getDescription(), foundSession.getDescription());
        assertEquals(session.getCreatedAt(), foundSession.getCreatedAt());
        assertEquals(session.getUpdatedAt(), foundSession.getUpdatedAt());
    }

    @Test
    public void testUpdateSession() {
        Teacher teacher = new Teacher();
        teacher.setId(1L).setFirstName("John").setLastName("Doe").setCreatedAt(LocalDateTime.now()).setUpdatedAt(LocalDateTime.now());

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user
                    .setId((long) i)
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setEmail("john.doe@gmail.com")
                    .setAdmin(false)
                    .setPassword("password");
        }
        Session original = new Session();
        original
                .setId(1L)
                .setDate(new Date())
                .setTeacher(teacher)
                .setUsers(users)
                .setName("name")
                .setDescription("description")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        Session edited = new Session();
        edited
                .setId(1L)
                .setDate(new Date())
                .setTeacher(teacher)
                .setUsers(users)
                .setName("edited-name")
                .setDescription("edited-description")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        when(sessionRepository.save(edited)).thenReturn(edited);

        Session updatedSession = sessionService.update(original.getId(), edited);

        assertEquals(edited.getId(), updatedSession.getId());
        assertEquals(edited.getDate(), updatedSession.getDate());
        assertEquals(edited.getTeacher(), updatedSession.getTeacher());
        assertEquals(edited.getUsers(), updatedSession.getUsers());
        assertEquals(edited.getName(), updatedSession.getName());
        assertEquals(edited.getDescription(), updatedSession.getDescription());
        assertEquals(edited.getCreatedAt(), updatedSession.getCreatedAt());
        assertEquals(edited.getUpdatedAt(), updatedSession.getUpdatedAt());
    }

    @Test
    public void testParticipateToSession() {
        Teacher teacher = new Teacher();
        teacher.setId(1L).setFirstName("John").setLastName("Doe").setCreatedAt(LocalDateTime.now()).setUpdatedAt(LocalDateTime.now());

        User participant = new User();
        participant
                .setId(99L)
                .setFirstName("John")
                .setLastName("Doe")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEmail("john.doe@gmail.com")
                .setAdmin(false)
                .setPassword("password");

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user
                    .setId((long) i)
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setEmail("john.doe@gmail.com")
                    .setAdmin(false)
                    .setPassword("password");
            users.add(user);
        }
        Session session = new Session();
        session
                .setId(1L)
                .setDate(new Date())
                .setTeacher(teacher)
                .setUsers(users)
                .setName("name")
                .setDescription("description")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(99L)).thenReturn(Optional.of(participant));

        assertFalse(session.getUsers().contains(participant));

        sessionService.participate(session.getId(), participant.getId());

        assertTrue(session.getUsers().contains(participant));
    }

    @Test
    public void testNoLongerParticipateToSession() {
        Teacher teacher = new Teacher();
        teacher.setId(1L).setFirstName("John").setLastName("Doe").setCreatedAt(LocalDateTime.now()).setUpdatedAt(LocalDateTime.now());

        User participant = new User();
        participant
                .setId(99L)
                .setFirstName("John")
                .setLastName("Doe")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setEmail("john.doe@gmail.com")
                .setAdmin(false)
                .setPassword("password");

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            User user = new User();
            user
                    .setId((long) i)
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setEmail("john.doe@gmail.com")
                    .setAdmin(false)
                    .setPassword("password");
            users.add(user);
        }
        users.add(participant);
        Session session = new Session();
        session
                .setId(1L)
                .setDate(new Date())
                .setTeacher(teacher)
                .setUsers(users)
                .setName("name")
                .setDescription("description")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertTrue(session.getUsers().contains(participant));

        sessionService.noLongerParticipate(session.getId(), participant.getId());

        assertFalse(session.getUsers().contains(participant));
    }


}
