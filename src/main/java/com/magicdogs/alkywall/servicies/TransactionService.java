package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.enums.TypeTransaction;
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

    public TransactionDTO sendMoney(TransactionSendMoneyDTO transactionSendMoneyDTO, CurrencyType currencyType, String userEmail) {
        var accountDestination = accountRepository.findById(transactionSendMoneyDTO.getDestinationIdAccount())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta destino no encontrada"));

        var userOrigin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var accountOrigin = accountRepository.findByIdAccountAndUser(transactionSendMoneyDTO.getOriginIdAccount(), userOrigin)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta origen no encontrada"));

        validateCurrencyType(accountOrigin, accountDestination, currencyType);
        validateBalanceAndLimit(accountOrigin, transactionSendMoneyDTO.getAmount());

        var transaction = processTransaction(accountOrigin, accountDestination, transactionSendMoneyDTO);

        return modelMapperConfig.transactionToDTO(transaction);
    }

    private void validateCurrencyType(Account accountOrigin, Account accountDestination, CurrencyType currencyType) {
        if (accountOrigin.getCurrency() != currencyType || accountDestination.getCurrency() != currencyType) {
            throw new ApiException(HttpStatus.CONFLICT, "Tipos de monedas distintos");
        }
    }

    private void validateBalanceAndLimit(Account accountOrigin, Double amount) {

        if (accountOrigin.getBalance() < amount && accountOrigin.getTransactionLimit() < amount) {
            if (accountOrigin.getCurrency() == CurrencyType.ARS) {
                throw new ApiException(HttpStatus.CONFLICT, "Balance y límite insuficiente. Tu límite es $ " + accountOrigin.getTransactionLimit());
            }
            if (accountOrigin.getCurrency() == CurrencyType.USD) {
                throw new ApiException(HttpStatus.CONFLICT, "Balance y límite insuficiente. Tu límite es US$ " + accountOrigin.getTransactionLimit());
            }
        }

        if (accountOrigin.getBalance() < amount) {
            throw new ApiException(HttpStatus.CONFLICT, "Balance insuficiente");
        }

        if (accountOrigin.getTransactionLimit() < amount) {
            if (accountOrigin.getCurrency() == CurrencyType.ARS) {
                throw new ApiException(HttpStatus.CONFLICT, "Límite insuficiente. Tu límite es $ " + accountOrigin.getTransactionLimit());
            }
            if (accountOrigin.getCurrency() == CurrencyType.USD) {
                throw new ApiException(HttpStatus.CONFLICT, "Límite insuficiente. Tu límite es US$ " + accountOrigin.getTransactionLimit());
            }
        }
    }

    private Transaction processTransaction(Account accountOrigin, Account accountDestination, TransactionSendMoneyDTO transactionSendMoneyDTO) {
        var transactionIncomeDestination = new Transaction(transactionSendMoneyDTO.getAmount(), TypeTransaction.INCOME, transactionSendMoneyDTO.getConcept(), transactionSendMoneyDTO.getDescription(), accountDestination);
        var transactionPaymentOrigin = new Transaction(transactionSendMoneyDTO.getAmount(), TypeTransaction.PAYMENT, transactionSendMoneyDTO.getConcept(), transactionSendMoneyDTO.getDescription(), accountOrigin);

        transactionRepository.save(transactionIncomeDestination);
        var transaction = transactionRepository.save(transactionPaymentOrigin);

        updateAccountBalances(accountOrigin, accountDestination, transactionSendMoneyDTO.getAmount());

        return transaction;
    }

    private void updateAccountBalances(Account accountOrigin, Account accountDestination, Double amount) {
        accountOrigin.setBalance(accountOrigin.getBalance() - amount);
        accountDestination.setBalance(accountDestination.getBalance() + amount);

        accountRepository.save(accountOrigin);
        accountRepository.save(accountDestination);
    }

    public List<TransactionDTO> getTransactionsByUserId(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!Objects.equals(user.getIdUser(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Usuario logueado distinto al usuario del id recibido");
        } else {
            List<TransactionDTO> transactions = new ArrayList<>();
            for (Account account : user.getAccounts()) {
                transactions.addAll(account.getTransactions().stream().map(modelMapperConfig::transactionToDTO).toList());
            }
            return transactions;
        }
    }

    public Optional<Page<TransactionDTO>> getTransactionsPageByUserId(Long id, String userEmail, int page, int size){
        if(page < 0 || size <= 0) throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!Objects.equals(user.getIdUser(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Usuario logueado distinto al usuario del id recibido");
        }
        Optional<Page<Transaction>> transactions = transactionRepository.findByAccountUserIdUser(id, PageRequest.of(page, size));
        return transactions.map(t -> t.map(modelMapperConfig::transactionToDTO));
    }

    public Optional<Page<TransactionDTO>> getTransactionsPageByUserAccountId(Long id, String userEmail, int page, int size){
        if(page < 0 || size <= 0) throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var account = accountRepository.findByIdAccountAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada para el usuario"));


        Optional<Page<Transaction>> transactions = transactionRepository.findByAccountIdAccountOrderByTransactionDateDesc(id, PageRequest.of(page, size));
        return transactions.map(t -> t.map(modelMapperConfig::transactionToDTO));
    }

    public TransactionAccountDTO deposit(TransactionDepositDTO deposit, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var account = user.getAccountIn(deposit.getAccountType(), deposit.getCurrency());
        if (account == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }

        var transaction = new Transaction(deposit.getAmount(), TypeTransaction.DEPOSIT, deposit.getConcept(), deposit.getDescription(), account);
        transactionRepository.save(transaction);

        account.setBalance(account.getBalance() + deposit.getAmount());
        accountRepository.save(account);

        var transactionDTO = modelMapperConfig.transactionToDTO(transaction);
        var accountDTO = modelMapperConfig.accountToDTO(account);

        return new TransactionAccountDTO(transactionDTO, accountDTO);
    }

    public TransactionAccountDTO payment(TransactionDepositDTO payment, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var account = user.getAccountIn(payment.getAccountType(), payment.getCurrency());
        if (account == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }

        validateBalanceAndLimit(account, payment.getAmount());

        var transaction = new Transaction(payment.getAmount(), TypeTransaction.PAYMENT, payment.getConcept(), payment.getDescription(), account);
        transactionRepository.save(transaction);

        account.setBalance(account.getBalance() - payment.getAmount());
        accountRepository.save(account);

        var transactionDTO = modelMapperConfig.transactionToDTO(transaction);
        var accountDTO = modelMapperConfig.accountToDTO(account);

        return new TransactionAccountDTO(transactionDTO, accountDTO);
    }

    public TransactionDTO getDetailsTreansactionById(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        var transaction = user.findTransactionByIdInAccount(id);
        if (transaction == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Id transaccion no corresponde al usuario logeado");
        }

        var transactionDTO = modelMapperConfig.transactionToDTO(transaction);

        return transactionDTO;
    }

    public TransactionDTO updateTransaction(Long idTransaction, TransactionUpdateDTO update, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var transaction = user.findTransactionByIdInAccount(idTransaction);

        if (transaction == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Id transaccion no corresponde al usuario logeado");
        }

        transaction.setDescription(update.getDescription());
        transactionRepository.save(transaction);

        return modelMapperConfig.transactionToDTO(transaction);
    }
}
