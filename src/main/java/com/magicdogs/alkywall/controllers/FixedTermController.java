package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.FixedTermCreateDTO;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.FixedTermService;
import com.magicdogs.alkywall.servicies.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fixedTerm")
@AllArgsConstructor
@Validated
public class FixedTermController {
    private final FixedTermService fixedTermService;
    private final JWTService jwtService;

    @PostMapping("")
    public ResponseEntity<?> createFixedTerm(@RequestBody @Valid FixedTermCreateDTO fixedTermCreateDTO, HttpServletRequest request) {

        var token = jwtService.getJwtFromCookies(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No token provided");
        }
        var userEmail = jwtService.extractUserId(token);
        var fixedTermDeposit = fixedTermService.createFixedTermDeposit(fixedTermCreateDTO, userEmail);
        return ResponseEntity.ok(fixedTermDeposit);

    }

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateFixedTerm (@RequestBody @Valid FixedTermCreateDTO fixedTermCreateDTO){
        var fixedTermDeposit = fixedTermService.simulateFixedTerm(fixedTermCreateDTO);
        return ResponseEntity.ok().body(fixedTermDeposit);
    }

    @Operation(summary = "Obtener todos los plazos fijos del usuario loggeado ordenados por fecha")
    @GetMapping("")
    public ResponseEntity<?> createAccount(@RequestParam(defaultValue = "0")int page,
                                           @RequestParam(defaultValue = "10")int size,
                                           HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var fixedTermsPage = fixedTermService.getFixedTerms(userEmail, page, size);
        return ResponseEntity.ok(fixedTermsPage);
    }

}
