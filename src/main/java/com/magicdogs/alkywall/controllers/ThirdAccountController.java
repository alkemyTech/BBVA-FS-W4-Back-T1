package com.magicdogs.alkywall.controllers;

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

@RestController
@RequestMapping("/cbu")
@AllArgsConstructor
@Tag(name = "Agenda", description = "Endpoints para la gestión de agenda de cbus de un usuario")

public class ThirdAccountController {
    private JWTService jwtService;
    private final ThirdAccountService thirdAccountService;

    @Operation(summary = "Agrego el cbu de la agenda")
    @PostMapping("")
    public ResponseEntity<?> addCBU(@RequestBody @Valid ThirdAccountDTO thirdAccountDTO, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var mensaje = thirdAccountService.creatThirdAccount(thirdAccountDTO, userEmail);

        return ResponseEntity.ok().body(mensaje);
    }

    @Operation(summary = "Elimino el cbu de la agenda")
    @DeleteMapping("/{cbu}")
    public ResponseEntity<?> deleteCBU(@PathVariable("cbu") @NotBlank @NotNull @NotEmpty String cbu, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var mensaje = thirdAccountService.deleteThirdAccount(cbu, userEmail);

        return ResponseEntity.ok().body(mensaje);


    }
}
