package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

    private TransactionService transactionService;
    private JWTService jwtService;

    @PostMapping("/sendArs")
    public ResponseEntity<?> sendArs(@RequestBody TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        transactionService.sendMoney(transactionDTO, CurrencyType.ARS, userEmail);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/sendUsd")
    public ResponseEntity<?> sendUsd(@RequestBody TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        transactionService.sendMoney(transactionDTO, CurrencyType.USD, userEmail);
        return ResponseEntity.ok().build();
    }

}
