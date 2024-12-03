package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTests {
    @Mock
    private TeacherRepository teacherRepository;

    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        this.teacherService = new TeacherService(teacherRepository);
    }

    @Test
    public void testFindAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            Teacher teacher = new Teacher();
            teacher
                    .setFirstName("firstname" + i)
                    .setLastName("lastname" + i)
                    .setId((long) i)
                    .setCreatedAt(LocalDateTime.now())
                    .setUpdatedAt(LocalDateTime.now());
            teachers.add(teacher);
        }

        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> teacherList = teacherService.findAll();

        assertEquals(10, teacherList.size());
    }

    @Test
    public void testFindTeacherById() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("firstname1");
        teacher.setLastName("lastname1");
        teacher.setId((long) 1);
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher foundTeacher = teacherService.findById(1L);

        assertEquals(teacher, foundTeacher);
    }
}
