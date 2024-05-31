package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.ListTransactionDTO;
import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.entities.TypeTransaction;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.TransactionRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TransactionService {
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;


    public void sendMoney(TransactionDTO transactionDTO, CurrencyType currencyType, String userEmail) {
        var accountDestination = accountRepository.findById(transactionDTO.getDestinationIdAccount())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta destino no encontrada"));

        var userOrigin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var accountOrigin = userOrigin.getAccountIn(currencyType);
        if (accountOrigin == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Cuenta origen no encontrada para la moneda indicada");
        }

        validateCurrencyType(accountOrigin, accountDestination, currencyType);
        validateBalanceAndLimit(accountOrigin, transactionDTO.getAmount());

        processTransaction(accountOrigin, accountDestination, transactionDTO);
    }

    private void validateCurrencyType(Account accountOrigin, Account accountDestination, CurrencyType currencyType) {
        if (accountOrigin.getCurrency() != currencyType || accountDestination.getCurrency() != currencyType) {
            throw new ApiException(HttpStatus.CONFLICT, "Tipos de monedas distintos");
        }
    }

    private void validateBalanceAndLimit(Account accountOrigin, double amount) {
        if (accountOrigin.getBalance() < amount) {
            throw new ApiException(HttpStatus.CONFLICT, "Balance insuficiente");
        }
        if (accountOrigin.getTransactionLimit() < amount) {
            throw new ApiException(HttpStatus.CONFLICT, "Limite insuficiente");
        }
    }

    private void processTransaction(Account accountOrigin, Account accountDestination, TransactionDTO transactionDTO) {
        var transactionIncomeDestination = new Transaction(transactionDTO.getAmount(), TypeTransaction.INCOME, transactionDTO.getDescription(), accountDestination);
        var transactionPaymentOrigin = new Transaction(transactionDTO.getAmount(), TypeTransaction.PAYMENT, transactionDTO.getDescription(), accountOrigin);

        transactionRepository.save(transactionIncomeDestination);
        transactionRepository.save(transactionPaymentOrigin);

        updateAccountBalances(accountOrigin, accountDestination, transactionDTO.getAmount());
    }

    private void updateAccountBalances(Account accountOrigin, Account accountDestination, double amount) {
        accountOrigin.setBalance(accountOrigin.getBalance() - amount);
        accountDestination.setBalance(accountDestination.getBalance() + amount);

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestination);
    }
/*
    public List<ListTransactionDTO> getTransactionsByUserId(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!Objects.equals(user.getIdUser(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Usuario logueado distinto al usuario del id recibido");
        } else {
            List<ListTransactionDTO> transactions = new ArrayList<>();
            for (Account account : user.getAccounts()) {
                transactions.addAll(account.getTransactions().stream().map(modelMapperConfig::listTransactionDTO).toList());
            }
            return transactions;

        }
    }*/

    public Optional<Page<ListTransactionDTO>> getTransactionsPageByUserId(Long id, int page, int size){
        Optional<Page<Transaction>> transactions = transactionRepository.findByAccountUserIdUser(id, PageRequest.of(page, size));
        return transactions.map(t -> t.map(modelMapperConfig::listTransactionDTO));
    }
}
