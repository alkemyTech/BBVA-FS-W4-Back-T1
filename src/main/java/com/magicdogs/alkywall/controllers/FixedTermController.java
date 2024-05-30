package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.FixedTermCreateDTO;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.FixedTermService;
import com.magicdogs.alkywall.servicies.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fixedTerm")
@AllArgsConstructor
@Validated
public class FixedTermController {
    private final FixedTermService fixedTermService;
    private final JWTService jwtService;

    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody @Valid FixedTermCreateDTO fixedTermCreateDTO, HttpServletRequest request) {
        var token = jwtService.getJwtFromCookies(request);
        var userEmail = jwtService.extractUserId(token);
        var fixedTermDeposit = fixedTermService.createFixedTermDeposit(fixedTermCreateDTO, userEmail);
        return ResponseEntity.ok(fixedTermDeposit);

    }

}
