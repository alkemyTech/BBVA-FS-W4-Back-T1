package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Transacciones", description = "Endpoints para realizar transacciones")
@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

    private TransactionService transactionService;
    private JWTService jwtService;

    @Operation(summary = "Realiza una transacción de envío en ARS")
    @PostMapping("/sendArs")
    public ResponseEntity<?> sendArs(@RequestBody @Valid TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        transactionService.sendMoney(transactionDTO, CurrencyType.ARS, userEmail);
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "Realiza una transacción de envío en USD")
    @PostMapping("/sendUsd")
    public ResponseEntity<?> sendUsd(@RequestBody @Valid TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        transactionService.sendMoney(transactionDTO, CurrencyType.USD, userEmail);
        return ResponseEntity.ok().build();
    }
    @Operation(summary ="Lista de transacciones de un usuario")
    @GetMapping("userId/{userId}")
    public ResponseEntity<List<ListTransactionDTO>> transactionListByUser(@PathVariable("userId") Long id,
                                                                          HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        List<ListTransactionDTO> transactions = transactionService.getTransactionsByUserId(id, userEmail);
        return ResponseEntity.ok().body(transactions);
    }
    @Operation(summary ="Detalle de transaccion de transacciones de un usuario")
    @GetMapping("id/{id}")
    public ResponseEntity<?> transactionDetailsByID(@PathVariable("id") Long id,
                                                                          HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        //List<ListTransactionDTO> transactions = transactionService.getTransactionsByUserId(id, userEmail);
        var transsaction = transactionService.getDetailsTreansactionById(id, userEmail);
        return ResponseEntity.ok(transsaction);
    }


    @Operation(summary = "Realiza un ingreso de dinero")
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody @Valid TransactionDepositDTO deposit, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var TransactionAccountDTO = transactionService.deposit(deposit, deposit.getCurrency(), userEmail);
        return ResponseEntity.ok(TransactionAccountDTO);
    }

    @Operation(summary = "Realiza un pago")
    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody @Valid TransactionDepositDTO payment, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var TransactionAccountDTO = transactionService.payment(payment, payment.getCurrency(), userEmail);
        return ResponseEntity.ok(TransactionAccountDTO);
    }

    @Operation(summary = "Actualizar transacción")
    @PutMapping("/{idTransaction}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long idTransaction, @RequestBody @Valid TransactionUpdateDTO update, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var transaction = transactionService.updateTransaction(idTransaction, update, userEmail);
        return ResponseEntity.ok(transaction);
    }

}
