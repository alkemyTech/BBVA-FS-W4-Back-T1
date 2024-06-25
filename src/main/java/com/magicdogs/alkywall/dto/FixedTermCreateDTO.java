package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermCreateDTO {

        @NotNull(message = "El monto no puede ser nulo")
        @Positive(message = "El monto debe ser mayor a cero")
        private Double amount;

        @NotNull(message = "La fecha no puede ser nula")
        @Future(message = "La fecha debe ser en el futuro")
        private LocalDate closingDate;
        // LE PONGO LA HORA ACTUAL
        // AÑO, MES, DIA

}
