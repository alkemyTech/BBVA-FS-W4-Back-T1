package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.dto.UserUpdateDTO;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.servicies.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<UserPageDTO> userList(@RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size){
       var usersPage = userService.getUsers(0, page, size);
       return ResponseEntity.ok(usersPage);
    }

    @Operation(summary = "Obtener lista de usuarios inactivos")
    @GetMapping("/inactive")
    public ResponseEntity<UserPageDTO> userListInactive(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size){
        var usersPage = userService.getUsers(1, page, size);
        return ResponseEntity.ok(usersPage);
    }

    @Operation(summary = "Eliminar usuario por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> userDelete(@PathVariable("id") Long id, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        userService.softDeleteUser(id, userEmail);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @Operation(summary = "Actualizar los datos de un usuario")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> userUpdate(@PathVariable("id") Long id, @RequestBody @Valid UserUpdateDTO user, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var userUpdated = userService.update(id, userEmail, user);
        return ResponseEntity.ok(userUpdated);
    }

    @Operation(summary = "Obtener un usuario segun su id")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> userDetails(@PathVariable Long id, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var userDto = userService.userDetails(id, userEmail);
        return ResponseEntity.ok().body(userDto);
    }
}
