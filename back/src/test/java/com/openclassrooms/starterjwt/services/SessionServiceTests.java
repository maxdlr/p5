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

}
