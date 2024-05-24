package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.servicies.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("{userId}")
    public ResponseEntity<List<AccountDTO>> accountListByUser(@PathVariable("userId") Long id){
        return ResponseEntity.ok(accountService.accountsByUser(id));
    }
}
