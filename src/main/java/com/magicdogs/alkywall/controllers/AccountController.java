package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import jakarta.servlet.http.HttpServletRequest;
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
    private JWTService jwtService;

    @GetMapping("{userId}")
    public ResponseEntity<Optional<List<AccountDTO>>> accountListByUser(@PathVariable("userId") Long id){

        return ResponseEntity.ok(accountService.accountsByUser(id));
    }

    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody Account account, HttpServletRequest request) {
        try {
            var token = jwtService.getJwtFromCookies(request);
            var userEmail = jwtService.extractUserId(token);

            return ResponseEntity.ok(accountService.createAccount(userEmail, account.getCurrency()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
