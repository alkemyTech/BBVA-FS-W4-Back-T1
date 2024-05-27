package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.servicies.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("{userId}")
    public ResponseEntity<Optional<List<AccountDTO>>> accountListByUser(@PathVariable("userId") Long id){
        Optional<List<AccountDTO>> optionalAccounts = accountService.accountsByUser(id);
        if(optionalAccounts.isPresent()){return ResponseEntity.ok(optionalAccounts);}
        else {throw new RuntimeException("No se encontraron cuentas para el usuario con ID "+id);}
    }

    @PostMapping("/")
    public ResponseEntity<AccountDTO> createAccount(User user, @RequestBody Account account) {
        AccountDTO accountDTO = accountService.createAccount(user, account.getCurrency());
        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("balance")
    public ResponseEntity<AccountBalanceDTO> accountBalance(){
        return ResponseEntity.ok(null);
    }
}
