package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.AccountBank;
import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountThirdDTO {
    @Schema(description = "ID de la cuenta")
    private Long idAccount;

    @Schema(description = "Tipo de cuenta")
    private AccountType accountType;

    @Schema(description = "Tipo de moneda de la cuenta")
    private CurrencyType currency;

    @Schema(description = "Banco de la cuenta")
    private AccountBank bank;

    @Schema(description = "CBU de la cuenta")
    private String cbu;

    @Schema(description = "Alias de la cuenta")
    private String alias;

    @Schema(description = "ID del usuario")
    private Long userIdUser;

    @Schema(description = "Nombre del usuario")
    private String userFirstName;

    @Schema(description = "Apellido del usuario")
    private String userLastName;

    @Schema(description = "Tipo de documento del usuario")
    private DocumentType userDocumentType;

    @Schema(description = "Número de documento del usuario")
    private String userDocumentNumber;
}
