package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.servicies.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios")
public class UserController {

    private final UserService userService;
    private JWTService jwtService;

    @Operation(summary = "Obtener lista de usuarios")
    @GetMapping("")
    public ResponseEntity<UserPageDTO> userList(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10")int size){
        Page<UserDto> usersPage = userService.getUsers(page, size);
        String next = "", prev = "";
        if(usersPage.hasNext()){
            next = "/users?page="+(page+1);
        }
        if(usersPage.hasPrevious()){
            prev = "/users?page="+(page-1);
        }
        return ResponseEntity.ok(new UserPageDTO(usersPage.getContent(), next, prev));
    }

    @Operation(summary = "Eliminar usuario por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> userDelete(@PathVariable("id") Long id, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        userService.softDeleteUser(id, userEmail);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
