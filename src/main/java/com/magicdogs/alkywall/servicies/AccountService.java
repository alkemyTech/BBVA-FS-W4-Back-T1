package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.AccountPageDTO;
import com.magicdogs.alkywall.dto.AccountThirdDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.enums.AccountBank;
import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.utils.AliasGenerator;
import com.magicdogs.alkywall.utils.CbuGenerator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;
    private final AliasGenerator aliasGenerator;
    private final CbuGenerator cbuGenerator;

    public AccountPageDTO accountsByUser(Long userId, Integer softDelete, Integer page, Integer size){
        if (page < 0 || size <= 0) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");
        }

        Optional<Page<Account>> accountsOptional = Optional.ofNullable(accountRepository.findByUserIdUserAndSoftDelete(userId, softDelete, PageRequest.of(page, size))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));

        int cant = accountsOptional.get().getTotalPages();

        if (cant <= page ) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "No existe el numero de pagina");
        }

        Optional<Page<AccountDTO>> optionalAccounts = accountsOptional.map(accounts -> accounts.map(modelMapperConfig::accountToDTO));
        String next = "", prev = "";

        if (!optionalAccounts.isPresent()  && optionalAccounts.get().isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron cuentas para el usuario con ID " + userId);
        }

        if (optionalAccounts.get().hasNext())
            {next = "/accounts/"+userId+"?page="+(page+1);
        }

        if (optionalAccounts.get().hasPrevious()) {
            prev = "/accounts/"+userId+"?page="+(page-1);
        }

        return new AccountPageDTO(optionalAccounts.get().getContent(), next, prev, cant);
    }

    public AccountDTO createAccount(String userEmail, AccountType accountType, CurrencyType currency) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Optional<Account> existingAccount = accountRepository.findByUserAndAccountTypeAndCurrency(user, accountType, currency);
        if (existingAccount.isPresent()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "La cuenta ya existe");
        }

        if (accountType.equals(AccountType.CUENTA_CORRIENTE) && currency.equals(CurrencyType.USD)) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "No es posible crear una cuenta corriente en dólares");
        }

        var account = new Account(accountType, currency, AccountBank.ALKYWALL, cbuGenerator.generateUniqueCbu(), aliasGenerator.generateUniqueAlias(user.getFirstName(), user.getLastName()), 0.0, 0.0, user, 0);

        if (currency == CurrencyType.ARS) {
                account.setTransactionLimit(Constants.getTransactionLimitArs());
        } else if (currency == CurrencyType.USD) {
                account.setTransactionLimit(Constants.getTransactionLimitUsd());
        }

        Account savedAccount = accountRepository.save(account);
        return modelMapperConfig.accountToDTO(savedAccount);
    }

    public AccountDTO updateAccount(Long id, String userEmail, Double transactionLimit) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var account = accountRepository.findByIdAccountAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La cuenta no existe"));

        account.setTransactionLimit(transactionLimit);

        Account savedAccount = accountRepository.save(account);
        return modelMapperConfig.accountToDTO(savedAccount);
    }

    public AccountThirdDTO searchAccount(String value) {
        Optional<Account> account = accountRepository.findByCbu(value);
        if (account.isEmpty()) {
            account = accountRepository.findByAlias(value);
        }

        Account foundAccount = account.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La cuenta no existe"));

        return modelMapperConfig.accountThirdToDTO(foundAccount);
    }

    /**
     * Retorna el balance de cada cuenta con
     * su historial de transacciones y plazos fijos.
     * Los balances se diferencian por su tipo de moneda
     * El balance en pesos argentinos puede tener caja de ahorro y cuenta corriente.
     * @param userEmail
     * @return DTO con el balance de las cuentas
     */
    public AccountBalanceDTO getAccountBalance(String userEmail){
        List<Account> accounts = accountRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuentas no encontradas"));

        if (accounts.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El usuario no tiene cuentas");
        }

        AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();

        for(Account ac: accounts){
            accountBalanceDTO.addHistory(ac.getTransactions().stream().map(modelMapperConfig::transactionToDTO).toList());
            accountBalanceDTO.addFixedTerms(ac.getFixedTermDeposits().stream().map(modelMapperConfig::fixedTermsBalanceToDTO).toList());

            if(ac.getCurrency().equals(CurrencyType.ARS)){
                accountBalanceDTO.getAccountArs().add(modelMapperConfig.accountToDTO(ac));
            } else if(ac.getCurrency().equals(CurrencyType.USD)) {
                accountBalanceDTO.setAccountUsd(modelMapperConfig.accountToDTO(ac));
            }
        }
        return accountBalanceDTO;
    }

    public Optional<List<Account>> getAccountsByUserEmail(String email){
        return accountRepository.findByUserEmail(email);
    }

    /**
     * Baja logica de una cuenta
     * @param id
     */
    public void softDeleteAccount(Long id, String email){
        var accountOptional = accountRepository.findByIdAccountAndUserEmail(id, email);

        if (accountOptional.isPresent()) {
            accountOptional.get().setSoftDelete(1);
            accountRepository.save(accountOptional.get());
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La cuenta no corresponde a este usuario");
        }
    }
}
