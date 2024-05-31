package com.magicdogs.alkywall.controllers;

import com.magicdogs.alkywall.dto.FixedTermCreateDTO;
import com.magicdogs.alkywall.dto.LoanSimulateDTO;
import com.magicdogs.alkywall.servicies.FixedTermService;
import com.magicdogs.alkywall.servicies.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Prestamos", description = "Endpoints para prestamos")
@RestController
@RequestMapping("/loan")
@AllArgsConstructor
@Validated
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Realiza la simulacion de un prestamo")
    @PostMapping("/simulate")
    public ResponseEntity<?> simulateLoan (@RequestBody @Valid LoanSimulateDTO loanSimulateDTO){
        var loan = loanService.simulate(loanSimulateDTO);
        return ResponseEntity.ok().body(loan);
    }

}
