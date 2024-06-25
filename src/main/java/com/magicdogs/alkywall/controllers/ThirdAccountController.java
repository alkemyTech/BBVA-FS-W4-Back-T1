package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.ThirdAccountCreateDTO;
import com.magicdogs.alkywall.dto.ThirdAccountDTO;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.ThirdAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cbu")
@AllArgsConstructor
@Tag(name = "Agenda", description = "Endpoints para la gestión de agenda de cbus de un usuario")

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

    @Operation(summary = "Agrego el cbu de la agenda")
    @PostMapping("")
    public ResponseEntity<?> addCBU(@RequestBody @Valid ThirdAccountCreateDTO thirdAccountCreateDTO, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var mensaje = thirdAccountService.createThirdAccount(thirdAccountCreateDTO, userEmail);
        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Elimino el cbu de la agenda")
    @DeleteMapping("/{cbu}")
    public ResponseEntity<?> deleteCBU(@PathVariable("cbu") Long id, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var mensaje = thirdAccountService.deleteThirdAccount(id, userEmail);
        return ResponseEntity.ok().body(mensaje);
    }
}
