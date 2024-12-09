package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class TeacherMapperTests {
    private TeacherMapper teacherMapper;

    @BeforeEach
    public void init() {
        teacherMapper = new TeacherMapperImpl();
    }

    @Test
    public void testToEntity() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");
        teacherDto.setCreatedAt(LocalDateTime.now());

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getCreatedAt(), teacher.getCreatedAt());
    }

    @Test
    public void testToDto() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());
        TeacherDto teacherDto = teacherMapper.toDto(teacher);
        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getCreatedAt(), teacher.getCreatedAt());
    }

    @Test
    public void testToDtoList() {

        List<Teacher> teachers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Teacher teacher = new Teacher();
            teacher.setId((long) i + 1);
            teacher.setFirstName("Teacher" + i);
            teacher.setLastName("Lastname" + i);
            teacher.setCreatedAt(LocalDateTime.now());
            teachers.add(teacher);
        }


        List<TeacherDto> teacherDtos = teacherMapper.toDto(teachers);


        assertEquals(5, teacherDtos.size(), "The size of the mapped list should be 5");
        for (int i = 0; i < 5; i++) {
            assertEquals(teachers.get(i).getId(), teacherDtos.get(i).getId());
            assertEquals(teachers.get(i).getFirstName(), teacherDtos.get(i).getFirstName());
            assertEquals(teachers.get(i).getLastName(), teacherDtos.get(i).getLastName());
            assertEquals(teachers.get(i).getCreatedAt(), teacherDtos.get(i).getCreatedAt());
        }
    }

    @Test
    public void testToEntityList() {

        List<TeacherDto> teacherDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            TeacherDto teacherDto = new TeacherDto();
            teacherDto.setId((long) i + 1);
            teacherDto.setFirstName("Teacher" + i);
            teacherDto.setLastName("Lastname" + i);
            teacherDto.setCreatedAt(LocalDateTime.now());
            teacherDtos.add(teacherDto);
        }


        List<Teacher> teachers = teacherMapper.toEntity(teacherDtos);


        assertEquals(5, teachers.size(), "The size of the mapped list should be 5");
        for (int i = 0; i < 5; i++) {
            assertEquals(teacherDtos.get(i).getId(), teachers.get(i).getId());
            assertEquals(teacherDtos.get(i).getFirstName(), teachers.get(i).getFirstName());
            assertEquals(teacherDtos.get(i).getLastName(), teachers.get(i).getLastName());
            assertEquals(teacherDtos.get(i).getCreatedAt(), teachers.get(i).getCreatedAt());
        }
    }
}
