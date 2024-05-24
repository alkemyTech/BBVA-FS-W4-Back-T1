package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.servicies.SecurityService;
import com.magicdogs.alkywall.servicies.UserService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
public class SecurityController {
   // private final AuthenticationService authenticationService;
    private static final String JWT_COOKIE_NAME = "jwt-token";
    private static final int JWT_EXPIRATION_MINUTES = 60;
    private SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO){

        try {
        //var token = authenticationService.login(loginRequest.user(), loginRequest.pass());
           // addJwtToCookie(response, token);
            System.out.println(userLoginDTO.toString());
            UserLoginDTO userEncontrado = securityService.login(userLoginDTO);
            System.out.println(userEncontrado.toString());
            if(userEncontrado.getAutenticado())  return ResponseEntity.ok().build();

       } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
       // return new UserLoginDTO();
    }

}
