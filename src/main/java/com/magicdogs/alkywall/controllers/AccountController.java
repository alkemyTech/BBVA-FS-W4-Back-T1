package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountCreateDTO;
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

        return ResponseEntity.ok(accountService.accountsByUser(id));
    }

    @PostMapping("/")
    public ResponseEntity<AccountCreateDTO> createAccount(User user, @RequestBody Account account) {
        AccountCreateDTO accountCreateDTO = accountService.createAccount(user, account.getCurrency());
        return ResponseEntity.ok(accountCreateDTO);
    }
}
