package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.Constants;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.AccountPageDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    public AccountPageDTO accountsByUser(Long userId, int page, int size){
        if(page < 0 || size <= 0) throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

        Optional<Page<Account>> accountsOptional = Optional.ofNullable(accountRepository.findByUserIdUser(userId, PageRequest.of(page, size))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));
        int cant = accountsOptional.get().getTotalPages();
        if(cant <= page )   throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "No existe el numero de pagina");
        Optional<Page<AccountDTO>> optionalAccounts = accountsOptional.map(accounts -> accounts.map(modelMapperConfig::accountToDTO));
        String next = "", prev = "";
        if (!optionalAccounts.isPresent()  && optionalAccounts.get().isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron cuentas para el usuario con ID " + userId);

        if(optionalAccounts.get().hasNext()){next = "/accounts/"+userId+"?page="+(page+1);}
        if(optionalAccounts.get().hasPrevious()){prev = "/accounts/"+userId+"?page="+(page-1);}
        return new AccountPageDTO(optionalAccounts.get().getContent(), next, prev, cant);


    }

    public AccountDTO createAccount(String userEmail, CurrencyType currency) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Optional<Account> existingAccount = accountRepository.findByUserAndCurrency(user, currency);
        if (existingAccount.isPresent()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "La cuenta ya existe");
        }

        var account = new Account(currency, 0.00, 0.00, user, false, generateUniqueCbu());

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

    public String generateUniqueCbu() {
        String cbu;
        do {
            cbu = generateCbu();
        } while (!isCbuUnique(cbu));
        return cbu;
    }

    private String generateCbu() {
        StringBuilder cbu = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 22; i++) {
            cbu.append(random.nextInt(10));
        }
        return cbu.toString();
    }

    private boolean isCbuUnique(String cbu) {
        return accountRepository.findByCbu(cbu).isEmpty();
    }

    /**
     * Retorna el balance de cada cuenta con
     * su historial de transacciones y plazos fijos.
     * Las cuentas se diferencian por su tipo de moneda
     * @param userEmail
     * @return DTO con el balance de las cuentas
     */
    public AccountBalanceDTO getAccountBalance(String userEmail){
        List<Account> accounts = accountRepository.findByUserEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuentas no encontradas"));
        if (accounts.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El usuario no tiene cuentas");
        }

        AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();

        for(Account ac: accounts){
            accountBalanceDTO.addHistory(ac.getTransactions().stream().map(modelMapperConfig::transactionBalanceToDTO).toList());
            accountBalanceDTO.addFixedTerms(ac.getFixedTermDeposits().stream().map(modelMapperConfig::fixedTermsBalanceToDTO).toList());
            if(ac.getCurrency().equals(CurrencyType.ARS)){
                accountBalanceDTO.setAccountArs(modelMapperConfig.accountToDTO(ac));
            }else if(ac.getCurrency().equals(CurrencyType.USD)){
                accountBalanceDTO.setAccountUsd(modelMapperConfig.accountToDTO(ac));
            }
        }
        return accountBalanceDTO;
    }

    public Optional<List<Account>> getAccountsByUserEmail(String email){
        return accountRepository.findByUserEmail(email);
    }
}
