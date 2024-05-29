package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.servicies.SecurityService;
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
public class SecurityController {

    private static final String JWT_COOKIE_NAME = "jwt-token";
    private static final int JWT_EXPIRATION_MINUTES = 60;
    private SecurityService securityService;

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
        var cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(JWT_EXPIRATION_MINUTES * 60);
        response.addCookie(cookie);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO registerRequest, HttpServletResponse response) {
        try {
            var newUser = securityService.registerUser(registerRequest);

            var userLoginDTO = new UserLoginDTO(registerRequest.getEmail(), registerRequest.getPassword());
            var token = securityService.login(userLoginDTO);
            addJwtToCookie(response, token);

            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody UserRegisterDTO registerRequest) {
        try {
            var newAdmin = securityService.registerAdmin(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
