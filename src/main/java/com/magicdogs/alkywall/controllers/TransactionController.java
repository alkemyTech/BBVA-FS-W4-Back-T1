package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.ListTransactionDTO;
import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

    private TransactionService transactionService;
    private JWTService jwtService;

    @PostMapping("/sendArs")
    public ResponseEntity<?> sendArs(@RequestBody @Valid TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        transactionService.sendMoney(transactionDTO, CurrencyType.ARS, userEmail);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/sendUsd")
    public ResponseEntity<?> sendUsd(@RequestBody @Valid TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        transactionService.sendMoney(transactionDTO, CurrencyType.USD, userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{userId}")
    public ResponseEntity<List<ListTransactionDTO>> transactionListByUser(@PathVariable("userId") Long id, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        List<ListTransactionDTO> transactions = transactionService.getTransactionsByUserId(id, userEmail);
        return ResponseEntity.ok().body(transactions);
    }

}
