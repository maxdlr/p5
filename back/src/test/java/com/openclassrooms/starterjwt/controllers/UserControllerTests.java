package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Authentication authentication;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService, userMapper);
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testFindUserById() throws Exception {
        User user = makeUser();
        UserDto userDto = makeUserDto();

        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mvc.perform(get("/api/user/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));

        mvc.perform(get("/api/user/{id}", 2L)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/user/{id}", "bad-request")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUserById() throws Exception {
        User user = makeUser();
        UserDetails userDetails = makeUserDetails(1);

        when(userService.findById(1L)).thenReturn(user);
        when(userService.findById(999L)).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        mvc.perform(delete("/api/user/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mvc.perform(delete("/api/user/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        userDetails = makeUserDetails(2);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        mvc.perform(delete("/api/user/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mvc.perform(delete("/api/user/{id}", "bad-request")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private User makeUser() {
        User user = new User();
        user
                .setAdmin(false)
                .setId((long) 1)
                .setEmail("email@email.com" + 1)
                .setPassword("password" + 1)
                .setFirstName("firstname" + 1)
                .setLastName("lastname" + 1)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserDto makeUserDto() {
        UserDto userDto = new UserDto();
        userDto .setId((long) 1);
        userDto.setAdmin(false);
        userDto.setEmail("email@email.com" + 1);
        userDto.setFirstName("firstname" + 1);
        userDto.setLastName("lastname" + 1);
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());
        return userDto;
    }

    private UserDetails makeUserDetails(int number) {
        return new UserDetailsImpl(
                (long) number,
                "email@email.com" + number,
                "firstname" + number,
                "lastname" + number,
                false,
                "password" + number
        );
    }
}
