package com.magicdogs.alkywall.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.controllers.UserController;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("Obtener lista de usuarios con paginación")
    @Test
    public void testUserList() throws Exception {
        // Crear un UserPageDTO mock
        UserPageDTO mockUserPage = new UserPageDTO();
        mockUserPage.setUsers(List.of(
                new UserDto("John", "Doe", 0, "john.doe@example.com"),
                new UserDto("Jane", "Smith", 0, "jane.smith@example.com")
        ));
        mockUserPage.setNextPage("/users?page=1&size=10");
        mockUserPage.setPrevPage(null);
        mockUserPage.setTotalPages(1);

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
                .andExpect(jsonPath("$.users[0].firstName").value("John"))
                .andExpect(jsonPath("$.users[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.users[0].softDelete").value(0))
                .andExpect(jsonPath("$.users[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.users[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.users[1].lastName").value("Smith"))
                .andExpect(jsonPath("$.users[1].softDelete").value(0))
                .andExpect(jsonPath("$.users[1].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.nextPage").value("/users?page=1&size=10"))
                .andExpect(jsonPath("$.prevPage").doesNotExist())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andReturn();

    }

    @DisplayName("Proporcionar valores no válidos para los parámetros de paginación")
    @Test
    public void testUserListInvalidPaginationParams() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", "invalid") // Valor no válido para page
                        .param("size", "invalid") // Valor no válido para size
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Espera un error 400 Bad Request
    }
}
