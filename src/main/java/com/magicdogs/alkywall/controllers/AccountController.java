package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.AccountUpdateDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final JWTService jwtService;


    @GetMapping("{userId}")
    public ResponseEntity<List<AccountDTO>> accountListByUser(@PathVariable("userId") Long id) {
        Optional<List<AccountDTO>> optionalAccounts = accountService.accountsByUser(id);
        if (optionalAccounts.isPresent() && !optionalAccounts.get().isEmpty()) {
            return ResponseEntity.ok(optionalAccounts.get());
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron cuentas para el usuario con ID " + id);
        }
    }

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

    @GetMapping("balance")
    public ResponseEntity<List<AccountBalanceDTO>> accountsBalance(HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        return ResponseEntity.ok(accountService.getAccountBalance(userEmail));
    }
}
