package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.servicies.SecurityService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.Cookie;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO,HttpServletResponse response){

       /* try {
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

*/
        try {
            var token = securityService.login(userLoginDTO);
            addJwtToCookie(response, token);

            // ESTO VA EN EL SERVICE?
            UserDto userReturn = securityService.searchUser(userLoginDTO);
            return ResponseEntity.ok(userReturn);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
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

}
