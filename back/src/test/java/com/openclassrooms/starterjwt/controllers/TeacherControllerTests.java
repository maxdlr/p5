package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTests {
    private MockMvc mvc;

    @Mock
    TeacherMapper teacherMapper;

    @Mock
    TeacherService teacherService;

    @BeforeEach
    public void setUp() {
        TeacherController teacherController = new TeacherController(teacherService, teacherMapper);

        mvc = MockMvcBuilders
                .standaloneSetup(teacherController)
                .build();
    }

    @Test
    public void testFindTeacherById() throws Exception {
        Teacher teacher = makeTeacher(1);
        TeacherDto teacherDto = makeTeacherDto(1);

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        mvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
        ;

        when(teacherService.findById(1L)).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
        ;

        mvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", "bad-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void testFindAllTeachers() throws Exception {

        List<Teacher> teachers = new ArrayList<>();
        List<TeacherDto> teacherDtos = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Teacher teacher = makeTeacher(i);
            TeacherDto teacherDto = makeTeacherDto(i);
            teachers.add(teacher);
            teacherDtos.add(teacherDto);
        }

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        mvc.perform(MockMvcRequestBuilders
                .get("/api/teacher")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(
                        jsonPath("$[" + (teacherDtos.size() - 1) + "].id")
                        .value(teacherDtos.get((teacherDtos.size() - 1)).getId().toString())
                );
    }

    private Teacher makeTeacher(int number) {
        Teacher teacher = new Teacher();
        teacher
                .setId((long) number)
                .setCreatedAt(LocalDateTime.now())
                .setFirstName("teacher" + number)
                .setLastName("teacher" + number);
        return teacher;
    }

    private TeacherDto makeTeacherDto(int number) {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId((long) number);
        teacherDto.setFirstName("teacher" + number);
        teacherDto.setLastName("teacher" + number);
        return teacherDto;
    }
}
