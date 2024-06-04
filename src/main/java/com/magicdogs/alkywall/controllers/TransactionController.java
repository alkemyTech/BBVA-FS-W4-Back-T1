package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.ListTransactionDTO;
import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.dto.TransactionPageDTO;
import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        var ListTransactionDTO =  transactionService.sendMoney(transactionDTO, CurrencyType.ARS, userEmail);
        return ResponseEntity.ok(ListTransactionDTO);
    }

    @Operation(summary = "Realiza una transacción de envío en USD")
    @PostMapping("/sendUsd")
    public ResponseEntity<?> sendUsd(@RequestBody @Valid TransactionDTO transactionDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var ListTransactionDTO = transactionService.sendMoney(transactionDTO, CurrencyType.USD, userEmail);
        return ResponseEntity.ok(ListTransactionDTO);
    }

    @Operation(summary ="Lista de transacciones de un usuario")
    @GetMapping("userId/{userId}")
    public ResponseEntity<?> transactionListByUser(@PathVariable("userId") Long id, @RequestParam(defaultValue = "0")int page,
                                                                          @RequestParam(defaultValue = "10")int size,
                                                                          HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        //List<ListTransactionDTO> transactions = transactionService.getTransactionsByUserId(id, userEmail);

        Optional<Page<ListTransactionDTO>> transactions = transactionService.getTransactionsPageByUserId(id,userEmail, page, size);
        String next = "", prev = "";
        if(!transactions.isPresent() && transactions.get().isEmpty()){
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron transacciones para el usuario con ID " + id);
        }
        if(transactions.get().hasNext()){next = "/transactions/userId/"+id+"?page="+(page+1);}
        if(transactions.get().hasPrevious()){prev = "/transactions/userId/"+id+"?page="+(page-1);}
        return ResponseEntity.ok(new TransactionPageDTO(transactions.get().getContent(), next, prev));

    }

    /*@GetMapping("/{userId}")
    public ResponseEntity<TransactionPageDTO> transactionPageByUser(@PathVariable("userId") Long id,
                                                                    @RequestParam(defaultValue = "0")int page,
                                                                    @RequestParam(defaultValue = "10")int size){
        Optional<Page<ListTransactionDTO>> transactions = transactionService.getTransactionsPageByUserId(id, page, size);
        String next = "", prev = "";
        if(transactions.isPresent() && !transactions.get().isEmpty()){
            if(transactions.get().hasNext()){next = "/transactions/user/"+id+"?page="+(page+1);}
            if(transactions.get().hasPrevious()){prev = "/transactions/user/"+id+"?page="+(page-1);}
            return ResponseEntity.ok(new TransactionPageDTO(transactions.get().getContent(), next, prev));
        } else {
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron transacciones para el usuario con ID " + id);
        }
    }*/

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
        var TransactionAccountDTO = transactionService.deposit(deposit, userEmail);
        return ResponseEntity.ok(TransactionAccountDTO);
    }

    @Operation(summary = "Realiza un pago")
    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody @Valid TransactionDepositDTO payment, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var TransactionAccountDTO = transactionService.payment(payment, userEmail);
        return ResponseEntity.ok(TransactionAccountDTO);
    }

    @Operation(summary = "Actualizar transacción")
    @PutMapping("/{idTransaction}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long idTransaction, @RequestBody @Valid TransactionUpdateDTO update, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var TransactionUpdateDTO = transactionService.updateTransaction(idTransaction, update, userEmail);
        return ResponseEntity.ok(TransactionUpdateDTO);
    }
}
