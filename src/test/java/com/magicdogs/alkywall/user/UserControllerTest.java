package com.magicdogs.alkywall.user;

import com.magicdogs.alkywall.controllers.UserController;
import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.servicies.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @DisplayName("Obtener lista de usuarios - Éxito")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUsersListSuccess() throws Exception {
        UserPageDTO mockUserPage = new UserPageDTO();
        mockUserPage.setUsers(List.of(
                new UserDTO(1L, "John", "Doe", LocalDate.of(1990, 1, 1), UserGender.MALE, DocumentType.DNI, "33456789", "john.doe@example.com"),
                new UserDTO(2L, "Jane", "Smith", LocalDate.of(1992, 2, 2), UserGender.FEMALE, DocumentType.DNI, "35987654", "jane.smith@example.com")
        ));
        mockUserPage.setNextPage("/users?page=1");
        mockUserPage.setPrevPage("");

        // Configurar el comportamiento del mock de userService
        when(userService.getUsers(anyInt(), anyInt(), anyInt())).thenReturn(mockUserPage);

        // Realizar la solicitud al controlador
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.users").exists())
                .andExpect(jsonPath("$.nextPage").value("/users?page=1"))
                .andExpect(jsonPath("$.prevPage").value(""))
                .andReturn();
    }

    @DisplayName("Obtener lista de usuario - Acceso denegado")
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testUsersListAccessDenied() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @DisplayName("Proporcionar valores no válidos para los parámetros de paginación")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUserListInvalidPaginationParams() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "invalid") // Valor no válido para page
                        .param("size", "invalid") // Valor no válido para size
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Espera un error 400 Bad Request
    }
}
