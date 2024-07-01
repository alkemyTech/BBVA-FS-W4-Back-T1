package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.dto.TransactionSendMoneyDTO;
import com.magicdogs.alkywall.dto.TransactionPageDTO;
import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.enums.TypeTransaction;
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
    public ResponseEntity<?> sendArs(@RequestBody @Valid TransactionSendMoneyDTO transactionSendMoneyDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var transactionDTO =  transactionService.sendMoney(transactionSendMoneyDTO, CurrencyType.ARS, userEmail);
        return ResponseEntity.ok(transactionDTO);
    }

    @Operation(summary = "Realiza una transacción de envío en USD")
    @PostMapping("/sendUsd")
    public ResponseEntity<?> sendUsd(@RequestBody @Valid TransactionSendMoneyDTO transactionSendMoneyDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var transactionDTO = transactionService.sendMoney(transactionSendMoneyDTO, CurrencyType.USD, userEmail);
        return ResponseEntity.ok(transactionDTO);
    }

    @Operation(summary ="Lista de transacciones de un usuario")
    @GetMapping("userId/{userId}")
    public ResponseEntity<?> transactionListByUser(@PathVariable("userId") Long id, @RequestParam(defaultValue = "0")int page,
                                                                          @RequestParam(defaultValue = "10")int size,
                                                                          HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        //List<ListTransactionDTO> transactions = transactionService.getTransactionsByUserId(id, userEmail);

        Optional<Page<TransactionDTO>> transactions = transactionService.getTransactionsPageByUserId(id,userEmail, page, size);
        String next = "", prev = "";
        if(transactions.isEmpty() || transactions.get().isEmpty()){
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron transacciones para el usuario con ID " + id);
        }
        if(transactions.get().hasNext()){next = "/transactions/userId/"+id+"?page="+(page+1);}
        if(transactions.get().hasPrevious()){prev = "/transactions/userId/"+id+"?page="+(page-1);}
        int count = transactions.get().getTotalPages();
        Double max = TransactionService.findMaxAmount2(transactions.get().toList());
        return ResponseEntity.ok(new TransactionPageDTO(transactions.get().getContent(), next, prev,count, max));
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
        var transaction = transactionService.getDetailsTreansactionById(id, userEmail);
        return ResponseEntity.ok(transaction);
    }


    @Operation(summary = "Realiza un ingreso de dinero")
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody @Valid TransactionDepositDTO deposit, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var transactionAccountDTO = transactionService.deposit(deposit, userEmail);
        return ResponseEntity.ok(transactionAccountDTO);
    }

    @Operation(summary = "Realiza un pago")
    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody @Valid TransactionDepositDTO payment, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var transactionAccountDTO = transactionService.payment(payment, userEmail);
        return ResponseEntity.ok(transactionAccountDTO);
    }

    @Operation(summary = "Actualizar transacción")
    @PutMapping("/{idTransaction}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long idTransaction, @RequestBody @Valid TransactionUpdateDTO update, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var transactionUpdateDTO = transactionService.updateTransaction(idTransaction, update, userEmail);
        return ResponseEntity.ok(transactionUpdateDTO);
    }

    @Operation(summary ="Lista de transacciones de la cuenta de un usuario")
    @GetMapping("userAccountId/{userAccountId}")
    public ResponseEntity<?> transactionListByUserAccount(@PathVariable("userAccountId") Long id,
                                                          @RequestParam(defaultValue = "0")int page,
                                                   @RequestParam(defaultValue = "10")int size,
                                                   HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);

        Optional<Page<TransactionDTO>> transactions = transactionService.getTransactionsPageByUserAccountId(id,userEmail, page, size);

        String next = "", prev = "";
        if(transactions.isEmpty() || transactions.get().isEmpty()){
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron transacciones para el usuario con ID " + id);
        }
        if(transactions.get().hasNext()){next = "/transactions/userAccountId/"+id+"?page="+(page+1);}
        if(transactions.get().hasPrevious()){prev = "/transactions/userAccountId/"+id+"?page="+(page-1);}
        int count = transactions.get().getTotalPages();
        Double max = TransactionService.findMaxAmount2(transactions.get().toList());
        return ResponseEntity.ok(new TransactionPageDTO(transactions.get().getContent(), next, prev,count, max));
    }

    @Operation(summary = "Lista de transacciones de la cuenta de un usuario con filtros.")
    @GetMapping("userAccountId/{userAccountId}/filters")
    public ResponseEntity<?> transactionListByUserAccountWithFilters(
            @PathVariable("userAccountId") Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String concept,
            HttpServletRequest request) {

        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);

        /*Optional<Page<TransactionDTO>> transactions = transactionService.getTransactionsPageByUserAccountIdWithFilters(
                id, userEmail, page, size, minAmount, maxAmount, type, concept);



        String next = transactions.get().hasNext() ? "/transactions/userAccountId/" + id + "?page=" + (page + 1) : "";
        String prev = transactions.get().hasPrevious() ? "/transactions/userAccountId/" + id + "?page=" + (page - 1) : "";
        int count = transactions.get().getTotalPages();*/

        return transactionService.getTransactionsPageByUserAccountIdWithFilters(
                id, userEmail, page, size, minAmount, maxAmount, type, concept);
    }

    @Operation(summary = "Obtener resumenes de transacciones por mes separado por pago, deposito o ingreso")
    @GetMapping("/{idAccount}/summaryPerMoth")
    public ResponseEntity<?> summaryPerMoth(@PathVariable("idAccount") Long id, HttpServletRequest request){
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var summaryPerMonth = transactionService.resumenPorMes(userEmail,id);
        //var AccountThirdDTO = accountService.searchAccount(value, userEmail);
        return ResponseEntity.ok(summaryPerMonth);
    }


}
