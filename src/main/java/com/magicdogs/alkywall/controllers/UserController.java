package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios")
public class UserController {

    private final UserService userService;
    private JWTService jwtService;

    @Operation(summary = "Obtener lista de usuarios")
    @GetMapping("")
    public ResponseEntity<List<UserDto>> userList(){
        return ResponseEntity.ok(userService.getUsers());
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
