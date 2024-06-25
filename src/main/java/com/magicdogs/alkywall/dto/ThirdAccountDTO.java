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
public class ThirdAccountDTO {

    @Schema(description = "ID del contacto")
    private Long idThirdAccount;

    @Schema(description = "Apodo del contacto")
    private String nickname;

    @Schema(description = "ID de la cuenta")
    private Long destinationAccountIdAccount;

    @Schema(description = "Tipo de cuenta")
    private AccountType destinationAccountAccountType;

    @Schema(description = "Tipo de moneda de la cuenta")
    private CurrencyType destinationAccountCurrency;

    @Schema(description = "Banco de la cuenta")
    private AccountBank destinationAccountBank;

    @Schema(description = "CBU de la cuenta")
    private String destinationAccountCbu;

    @Schema(description = "Alias de la cuenta")
    private String destinationAccountAlias;

    @Schema(description = "ID del usuario")
    private Long destinationUserIdUser;

    @Schema(description = "Nombre del usuario")
    private String destinationUserFirstName;

    @Schema(description = "Apellido del usuario")
    private String destinationUserLastName;

    @Schema(description = "Tipo de documento del usuario")
    private DocumentType destinationUserDocumentType;

    @Schema(description = "Número de documento del usuario")
    private String destinationUserDocumentNumber;
}