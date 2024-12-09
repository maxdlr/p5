package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTests {
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    public void testToEntity() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@gmail.com");
        userDto.setPassword("password");
        userDto.setCreatedAt(LocalDateTime.now());

        User user = userMapper.toEntity(userDto);

        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.getCreatedAt(), user.getCreatedAt());
    }

    @Test
    public void testToDto() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@gmail.com");
        user.setPassword("password");
        user.setCreatedAt(LocalDateTime.now());

        UserDto userDto = userMapper.toDto(user);

        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.getCreatedAt(), user.getCreatedAt());
    }

    @Test
    public void testToDtoList() {

        List<User> users = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setFirstName("User" + i);
            user.setLastName("Lastname" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPassword("password" + i);
            user.setCreatedAt(LocalDateTime.now());

            users.add(user);
        }


        List<UserDto> userDtos = userMapper.toDto(users);


        assertEquals(5, userDtos.size(), "The size of the mapped list should be 5");
        for (int i = 0; i < 5; i++) {
            assertEquals(users.get(i).getFirstName(), userDtos.get(i).getFirstName());
            assertEquals(users.get(i).getLastName(), userDtos.get(i).getLastName());
            assertEquals(users.get(i).getEmail(), userDtos.get(i).getEmail());
            assertEquals(users.get(i).getPassword(), userDtos.get(i).getPassword());
            assertEquals(users.get(i).getCreatedAt(), userDtos.get(i).getCreatedAt());
        }
    }

    @Test
    public void testToEntityList() {

        List<UserDto> userDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            UserDto userDto = new UserDto();
            userDto.setFirstName("User" + i);
            userDto.setLastName("Lastname" + i);
            userDto.setEmail("user" + i + "@example.com");
            userDto.setPassword("password" + i);
            userDto.setCreatedAt(LocalDateTime.now());

            userDtos.add(userDto);
        }


        List<User> users = userMapper.toEntity(userDtos);


        assertEquals(5, users.size(), "The size of the mapped list should be 5");
        for (int i = 0; i < 5; i++) {
            assertEquals(userDtos.get(i).getFirstName(), users.get(i).getFirstName());
            assertEquals(userDtos.get(i).getLastName(), users.get(i).getLastName());
            assertEquals(userDtos.get(i).getEmail(), users.get(i).getEmail());
            assertEquals(userDtos.get(i).getPassword(), users.get(i).getPassword());
            assertEquals(userDtos.get(i).getCreatedAt(), users.get(i).getCreatedAt());
        }
    }
}
