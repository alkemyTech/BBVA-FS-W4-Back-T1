package com.magicdogs.alkywall.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.UserController;
import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.dto.UserUpdateDTO;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String email;
    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        userId = 1L;
        token = "mockedToken";
        email = "mockedUser@example.com";

        UserDTO userDto = new UserDTO(1L, "nombre", "apellido", LocalDate.of(1992, 2, 2), UserGender.MALE, DocumentType.DNI, "35987654", "user@email.com");
        when(jwtService.getJwtFromCookies(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtService.extractUserId(token)).thenReturn(email);
        when(userService.update(anyLong(), anyString(), any(UserUpdateDTO.class))).thenReturn(userDto);
    }

    @DisplayName("Test exitoso")
    @Test
    @WithMockUser
    public void testUserUpdate() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("nombre", "apellido", LocalDate.of(1990, 1, 1), UserGender.MALE, "35987654", "password");
        mockMvc.perform(put("/users/"+userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("Test url erronea")
    @Test
    @WithMockUser
    public void testUserUpdateNotFound() throws Exception {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("nombre", "apellido", LocalDate.of(1990, 1, 1), UserGender.MALE, "35987654", "password");
        mockMvc.perform(put("/user/"+userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
