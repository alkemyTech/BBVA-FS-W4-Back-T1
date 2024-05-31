package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Tag(name = "Cuentas", description = "Endpoints para la gestión de cuentas")
public class AccountController {

    private final AccountService accountService;
    private final JWTService jwtService;

    @Operation(summary = "Obtener lista de cuentas por ID de usuario")
    @GetMapping("{userId}")
    public ResponseEntity<AccountPageDTO> accountListByUser(@PathVariable("userId") Long id,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Optional<Page<AccountDTO>> optionalAccounts = accountService.accountsByUser(id, page, size);
        String next = "", prev = "";
        if (optionalAccounts.isPresent()  && !optionalAccounts.get().isEmpty()) {
            if(optionalAccounts.get().hasNext()){next = "/accounts/"+id+"?page="+(page+1);}
            if(optionalAccounts.get().hasPrevious()){prev = "/accounts/"+id+"?page="+(page-1);}
            return ResponseEntity.ok(new AccountPageDTO(optionalAccounts.get().getContent(), next, prev));
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron cuentas para el usuario con ID " + id);
        }
    }

    @Operation(summary = "Crear una nueva cuenta")
    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountCreateDTO account, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var accountDTO = accountService.createAccount(userEmail, account.getCurrency());
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
    @GetMapping("balance")
    public ResponseEntity<List<AccountBalanceDTO>> accountsBalance(HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        return ResponseEntity.ok(accountService.getAccountBalance(userEmail));
    }
}
