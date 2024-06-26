package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.ThirdAccountCreateDTO;
import com.magicdogs.alkywall.dto.ThirdAccountDTO;
import com.magicdogs.alkywall.dto.ThirdAccountUpdateDTO;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.ThirdAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
@AllArgsConstructor
@Tag(name = "Lista de contactos", description = "Endpoints para la gestión de lista de contactos de un usuario")

public class ThirdAccountController {
    private JWTService jwtService;
    private final ThirdAccountService thirdAccountService;

    @Operation(summary = "Obtengo lista de contactos")
    @GetMapping("")
    public ResponseEntity<?> getThirdAccounts(HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        List<ThirdAccountDTO> thirdAccountDTO = thirdAccountService.getThirdAccount(userEmail);
        return ResponseEntity.ok(thirdAccountDTO);
    }

    @Operation(summary = "Agrego cuenta en la lista de contactos")
    @PostMapping("")
    public ResponseEntity<?> addThirdAccount(@RequestBody @Valid ThirdAccountCreateDTO thirdAccountCreateDTO, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var message = thirdAccountService.createThirdAccount(thirdAccountCreateDTO, userEmail);
        return ResponseEntity.ok().body(message);
    }

    @Operation(summary = "Elimino cuenta en la lista de contactos")
    @DeleteMapping("/{idThirdAccount}")
    public ResponseEntity<?> deleteThirdAccount(@PathVariable("idThirdAccount") Long idThirdAccount, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var message = thirdAccountService.deleteThirdAccount(idThirdAccount, userEmail);
        return ResponseEntity.ok().body(message);
    }

    @Operation(summary = "Actualizo referencia de una cuenta")
    @PutMapping("")
    public ResponseEntity<?> updateThirdAccount(@RequestBody @Valid ThirdAccountUpdateDTO thirdAccountUpdateDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var message = thirdAccountService.updateThirdAccount(thirdAccountUpdateDTO, userEmail);
        return ResponseEntity.ok().body(message);
    }
}
