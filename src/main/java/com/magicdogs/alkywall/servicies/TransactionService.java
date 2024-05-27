package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.entities.TypeTransaction;
import com.magicdogs.alkywall.exceptions.AccountNotFoundException;
import com.magicdogs.alkywall.exceptions.NotEnoughException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.TransactionRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransactionService {
    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private UserRepository userRepository;


    public void sendMoney(TransactionDTO transactionDTO, CurrencyType currencyType, String userEmail) {
        var account_destination = accountRepository.findById(transactionDTO.getDestinationIdAccount());
        var user_origin = userRepository.findByEmail(userEmail);
        if (user_origin.isPresent()) {
            var account_origin = user_origin.get().getAccountIn(currencyType);
            if (account_destination.isPresent() && account_origin != null) {
                if (account_origin.getBalance() >= transactionDTO.getAmount() ) {
                    if (account_origin.getTransactionLimit() >=  transactionDTO.getAmount()) {
                        var transaction_income_destination = new Transaction(transactionDTO.getAmount(), TypeTransaction.INCOME, transactionDTO.getDescription(), account_destination.get());
                        var transaction_payment_origin = new Transaction(transactionDTO.getAmount(), TypeTransaction.PAYMENT, transactionDTO.getDescription(), account_origin);
                        transactionRepository.save(transaction_income_destination);
                        transactionRepository.save(transaction_payment_origin);

                        // Actualizamos el balance de la cuenta como si fuese el saldo:
                        account_origin.setBalance(account_origin.getBalance() - transactionDTO.getAmount());
                        accountRepository.save(account_origin);
                        account_destination.get().setBalance(account_destination.get().getBalance() + transactionDTO.getAmount());
                        accountRepository.save(account_destination.get());
                    } else {
                        throw new NotEnoughException("Not enough limit");
                    }
                } else {
                    throw new NotEnoughException("Not enough balance");
                }
            } else {
                throw new AccountNotFoundException("Account not found");
            }
        }

    }
}
