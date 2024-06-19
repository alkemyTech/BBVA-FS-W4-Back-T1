package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.Cookie;
import org.springframework.validation.annotation.Validated;
import com.magicdogs.alkywall.dto.UserRegisterDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
@AllArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para la autenticación de usuarios")
public class SecurityController {

    private SecurityService securityService;

    @Operation(summary = "Iniciar sesión de usuario")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO userLoginDTO,HttpServletResponse response){
        try {
            var token = securityService.login(userLoginDTO);
            addJwtToCookie(response, token);

            UserDto userReturn = securityService.searchUser(userLoginDTO);
            return ResponseEntity.ok(userReturn);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña invalido: " +e.getMessage() );
        }
    }


    private void addJwtToCookie(HttpServletResponse response, String token) {
        var cookie = new Cookie(Constants.getJwtCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.getJwtExpirationMinutes() * 60);
        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + token);
    }

    @Operation(summary = "Registrar nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO registerRequest, HttpServletResponse response) {
        var newUser = securityService.registerUser(registerRequest);
        var userLoginDTO = new UserLoginDTO(registerRequest.getEmail(), registerRequest.getPassword());
        var token = securityService.login(userLoginDTO);
        addJwtToCookie(response, token);
        return ResponseEntity.ok(newUser);
    }

    @Operation(summary = "Registrar nuevo usuario administrador")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid UserRegisterDTO registerRequest) {
        var newAdmin = securityService.registerAdmin(registerRequest);
        return ResponseEntity.ok(newAdmin);
    }
}
