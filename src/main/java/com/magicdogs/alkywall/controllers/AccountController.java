package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Tag(name = "Cuentas", description = "Endpoints para la gestión de cuentas")
@Validated
public class AccountController {

    private final AccountService accountService;
    private final JWTService jwtService;

    @Operation(summary = "Obtener lista de cuentas por ID de usuario")
    @GetMapping("/{userId}")

    public ResponseEntity<?> accountListByUser(@PathVariable("userId") Long id,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        var accountPageDTO = accountService.accountsByUser(id, page, size);
        return ResponseEntity.ok(accountPageDTO);
    }

    @Operation(summary = "Crear una nueva cuenta")
    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountCreateDTO account, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var accountDTO = accountService.createAccount(userEmail, account.getAccountType(), account.getCurrency());
        return ResponseEntity.ok(accountDTO);
    }

    @PutMapping("/{idAccount}")
    public ResponseEntity<?> updateAccount(@PathVariable("idAccount") Long id, @RequestBody @Valid AccountUpdateDTO account, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var accountDTO = accountService.updateAccount(id, userEmail, account.getTransactionLimit());
        return ResponseEntity.ok(accountDTO);
    }

    @Operation(summary = "Obtener balance de cuentas")
    @GetMapping("/balance")
    public ResponseEntity<?> accountsBalance(HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        return ResponseEntity.ok(accountService.getAccountBalance(userEmail));
    }
}
