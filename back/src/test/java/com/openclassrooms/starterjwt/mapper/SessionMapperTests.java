package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SessionMapperTests {

    @Mock
    private TeacherService teacherService;
    @Mock private UserService userService;

    private SessionMapper sessionMapper;

    @BeforeEach
    void setUp() {
        sessionMapper = new SessionMapperImpl();
        sessionMapper.teacherService = teacherService;
        sessionMapper.userService = userService;
    }

    @Test
    public void testToEntity() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());

        List<Long> users = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            users.add((long) i);
        }

        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("name");
        sessionDto.setDescription("description");
        sessionDto.setUsers(users);
        sessionDto.setTeacher_id(1L);
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setUpdatedAt(LocalDateTime.now());

        when(teacherService.findById(any(Long.class))).thenReturn(teacher);
        when(userService.findById(any(Long.class))).thenReturn(new User());

        Session session = sessionMapper.toEntity(sessionDto);

        assertEquals(session.getTeacher().getId(), teacher.getId());
        assertEquals(sessionDto.getName(), session.getName());
        assertEquals(sessionDto.getDescription(), session.getDescription());
        assertEquals(sessionDto.getCreatedAt(), session.getCreatedAt());
        assertEquals(sessionDto.getUpdatedAt(), session.getUpdatedAt());
    }

    @Test
    public void testToDto() {
        Session session = new Session();

        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());

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

    session
        .setId(1L)
        .setName("John")
        .setDescription("description")
        .setCreatedAt(LocalDateTime.now())
        .setUpdatedAt(LocalDateTime.now())
        .setDate(new Date()).setTeacher(teacher).setUsers(users);

        SessionDto sessionDto = sessionMapper.toDto(session);

        assertEquals(sessionDto.getTeacher_id(), teacher.getId());
        assertEquals(sessionDto.getName(), session.getName());
        assertEquals(sessionDto.getDescription(), session.getDescription());
        assertEquals(sessionDto.getCreatedAt(), session.getCreatedAt());
        assertEquals(sessionDto.getUpdatedAt(), session.getUpdatedAt());
        assertEquals(sessionDto.getDate(), session.getDate());
    }

    @Test
    public void testToDtoList() {
        List<Session> sessions = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Teacher teacher = new Teacher();
            teacher.setId((long) i);
            teacher.setFirstName("Teacher" + i);
            teacher.setLastName("Lastname" + i);
            teacher.setCreatedAt(LocalDateTime.now());

            Session session = new Session();
            session.setId((long) i)
                    .setName("Session " + i)
                    .setDescription("Description " + i)
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now())
                    .setDate(new Date())
                    .setTeacher(teacher)
                    .setUsers(new ArrayList<>());

            sessions.add(session);
        }

        List<SessionDto> sessionDtos = sessionMapper.toDto(sessions);

        assertEquals(5, sessionDtos.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(sessions.get(i).getName(), sessionDtos.get(i).getName());
            assertEquals(sessions.get(i).getDescription(), sessionDtos.get(i).getDescription());
        }
    }

    @Test
    public void testToEntityList() {
        List<SessionDto> sessionDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            List<Long> users = new ArrayList<>();
            users.add((long) i);

            SessionDto sessionDto = new SessionDto();
            sessionDto.setName("Session " + i);
            sessionDto.setDescription("Description " + i);
            sessionDto.setUsers(users);
            sessionDto.setTeacher_id((long) i);
            sessionDto.setCreatedAt(LocalDateTime.now());
            sessionDto.setUpdatedAt(LocalDateTime.now());

            sessionDtos.add(sessionDto);
        }

        when(teacherService.findById(any(Long.class))).thenReturn(new Teacher());
        when(userService.findById(any(Long.class))).thenReturn(new User());

        List<Session> sessions = sessionMapper.toEntity(sessionDtos);

        assertEquals(5, sessions.size(), "The size of the mapped list should be 5");
        for (int i = 0; i < 5; i++) {
            assertEquals(sessionDtos.get(i).getName(), sessions.get(i).getName());
            assertEquals(sessionDtos.get(i).getDescription(), sessions.get(i).getDescription());
        }
    }
}
