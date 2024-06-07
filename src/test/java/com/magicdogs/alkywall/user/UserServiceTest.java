package com.magicdogs.alkywall.user;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUsers() {
        // Mock UserRepository
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        Page<User> userPage = new PageImpl<>(userList);
        when(userRepository.findAllBySoftDelete(anyInt(), any())).thenReturn(userPage);

        // Test getUsers
        Integer softDelete = 0;
        Integer page = 0;
        Integer size = 10;
        UserPageDTO userPageDTO = userService.getUsers(softDelete, page, size);

        // Asserts
        assertEquals(2, userPageDTO.getUsers().size());
        assertEquals("", userPageDTO.getNextPage());
        assertEquals("", userPageDTO.getPrevPage());
        assertEquals(1, userPageDTO.getTotalPages());

        // Verify interactions
        verify(userRepository, times(1)).findAllBySoftDelete(softDelete, PageRequest.of(page, size));
        verify(modelMapperConfig, times(2)).userToDTO(any());
    }

    @Test
    public void testGetUsers_TotalPagesEqualsPage() {
        // Mock UserRepository
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        Page<User> userPage = new PageImpl<>(userList);
        when(userRepository.findAllBySoftDelete(anyInt(), any())).thenReturn(userPage);

        // Test getUsers
        Integer softDelete = 0;
        Integer page = 0;
        Integer size = 2; // Setting size to 2 to make totalPages equal to page
        UserPageDTO userPageDTO = userService.getUsers(softDelete, page, size);

        // Asserts
        assertEquals(2, userPageDTO.getUsers().size());
        assertEquals("", userPageDTO.getNextPage());
        assertEquals("", userPageDTO.getPrevPage());
        assertEquals(1, userPageDTO.getTotalPages());

        // Verify interactions
        verify(userRepository, times(1)).findAllBySoftDelete(softDelete, PageRequest.of(page, size));
        verify(modelMapperConfig, times(2)).userToDTO(any());
    }

    @Test
    public void testGetUsers_TotalPagesLessThanPage() {
        // Mock UserRepository
        Page<User> userPage = new PageImpl<>(List.of());
        when(userRepository.findAllBySoftDelete(anyInt(), any())).thenReturn(userPage);

        // Test getUsers
        Integer softDelete = 0;
        Integer page = 1; // Setting page greater than totalPages
        Integer size = 10;

        // Asserts and Exception handling
        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.getUsers(softDelete, page, size);
        });
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.getStatus());
        assertEquals("No existe el numero de pagina", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findAllBySoftDelete(softDelete, PageRequest.of(page, size));
        verify(modelMapperConfig, never()).userToDTO(any());
    }


}

