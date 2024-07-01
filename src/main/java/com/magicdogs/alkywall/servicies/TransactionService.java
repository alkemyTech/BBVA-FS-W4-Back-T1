package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.enums.TypeTransaction;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.TransactionRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                throw new ApiException(HttpStatus.CONFLICT, "Saldo y límite insuficiente. Tu límite es $ " + accountOrigin.getTransactionLimit());
            }
            if (accountOrigin.getCurrency() == CurrencyType.USD) {
                throw new ApiException(HttpStatus.CONFLICT, "Saldo y límite insuficiente. Tu límite es US$ " + accountOrigin.getTransactionLimit());
            }
        }

        if (accountOrigin.getBalance() < amount) {
            throw new ApiException(HttpStatus.CONFLICT, "Saldo insuficiente");
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

    public Optional<Page<TransactionDTO>> getTransactionsPageByUserId(Long id, String userEmail, int page, int size) {
        if (page < 0 || size <= 0)
            throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (!Objects.equals(user.getIdUser(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Usuario logueado distinto al usuario del id recibido");
        }
        Optional<Page<Transaction>> transactions = transactionRepository.findByAccountUserIdUser(id, PageRequest.of(page, size));
        return transactions.map(t -> t.map(modelMapperConfig::transactionToDTO));
    }

    public Optional<Page<TransactionDTO>> getTransactionsPageByUserAccountId(Long id, String userEmail, int page, int size) {
        if (page < 0 || size <= 0)
            throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

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
    public ResponseEntity<?> getTransactionsPageByUserAccountIdWithFilters(
            Long id,
            String userEmail,
            int page,
            int size,
            Double minAmount,
            Double maxAmount,
            String type,
            String concept) {

        if(page < 0 || size <= 0) throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var account = accountRepository.findByIdAccountAndUser(id, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada para el usuario"));


        Optional<List<Transaction>> transactions = transactionRepository.findByAccountIdAccountOrderByTransactionDateDesc(id);
        if(transactions.isEmpty()){
            throw new ApiException(HttpStatus.NOT_FOUND, "No se encontraron transacciones para el usuario con ID " + id);
        }
        List<Transaction> transactionsFiltered = transactions.get();
        if(minAmount != null){
            transactionsFiltered = transactionsFiltered.stream().filter(t -> t.getAmount() >= minAmount).toList();

        }
        if(maxAmount != null){
            transactionsFiltered = transactionsFiltered.stream().filter(t -> t.getAmount() <= maxAmount).toList();
        }
        if(type != null){
            transactionsFiltered = transactionsFiltered.stream().filter(t -> t.getType().name().equalsIgnoreCase(type)).toList();
        }
        if(concept != null){
            transactionsFiltered = transactionsFiltered.stream().filter(t -> t.getConcept().name().equalsIgnoreCase(concept)).toList();
        }

        int countPages = (int) Math.ceil((double) transactionsFiltered.size() / size);

        // Asegurar que el índice de la página no esté fuera de rango
        if (page >= countPages) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El número de página está fuera del rango.");
        }

        Double amountMax = findMaxAmount(transactions.get());
        // Calcular el índice de inicio y fin para la sublista
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, transactionsFiltered.size());
        List<Transaction> transactionsPage = transactionsFiltered.subList(fromIndex, toIndex);

        StringBuilder prev = new StringBuilder();
        StringBuilder next = new StringBuilder();

        String queryParams = buildQueryParams(minAmount, maxAmount, type, concept, size);

        if(page > 0 && countPages > 1){
            prev.append("/transactions/userAccountId/").append(id).append("/filters?page=").append(page - 1).append(queryParams);
        }
        if(countPages > 1 && page < countPages - 1){
            next.append("/transactions/userAccountId/").append(id).append("/filters?page=").append(page + 1).append(queryParams);
        }

        List<TransactionDTO> transactionDTOS = transactionsPage.stream().map(modelMapperConfig::transactionToDTO).toList();
        return ResponseEntity.ok(new TransactionPageDTO(transactionDTOS, next.toString(), prev.toString(), countPages, amountMax));
    }
    public static double findMaxAmount(List<Transaction> transactionsFiltered) {
        return transactionsFiltered.stream()
                .max(Comparator.comparingDouble(Transaction::getAmount))
                .orElseThrow(() -> new IllegalArgumentException("La lista está vacía"))
                .getAmount();
    }
    public static double findMaxAmount2(List<TransactionDTO> transactionsFiltered) {
        return transactionsFiltered.stream()
                .max(Comparator.comparingDouble(TransactionDTO::getAmount))
                .orElseThrow(() -> new IllegalArgumentException("La lista está vacía"))
                .getAmount();
    }


    private String buildQueryParams(Double minAmount, Double maxAmount, String type, String concept, int size) {
        StringBuilder queryParams = new StringBuilder();

        if(minAmount != null) queryParams.append("&minAmount=").append(minAmount);
        if(maxAmount != null) queryParams.append("&maxAmount=").append(maxAmount);
        if(type != null) queryParams.append("&type=").append(type);
        if(concept != null) queryParams.append("&concept=").append(concept);
        queryParams.append("&size=").append(size);

        return queryParams.toString();
    }

    public List<TransactionsSummaryPerMonthDTO> resumenPorMes(String email, Long id) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Optional<Account> accountOptional = accountRepository.findById(id);
        Account account = accountOptional.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La cuenta no existe"));
        if (!account.getUser().getIdUser().equals(user.getIdUser())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "No puedes ingresar una cuenta que no es propia");
        }

        Optional<List<Transaction>> transactionsOptional = transactionRepository.findByAccountIdAccountOrderByTransactionDate(id);
        List<Transaction> transactions = transactionsOptional.orElse(Collections.emptyList());

        // Obtener la fecha actual y la fecha hace 12 meses
        LocalDate currentDate = LocalDate.now();
        LocalDate twelveMonthsAgo = currentDate.minusMonths(11).withDayOfMonth(1);

        // Crear un mapa para almacenar el resumen por mes y tipo de transacción
        Map<String, TransactionsSummaryPerMonthDTO> summaryMap = new LinkedHashMap<>();

        // Inicializar el mapa con los últimos 12 meses
        for (int i = 0; i < 12; i++) {
            LocalDate month = twelveMonthsAgo.plusMonths(i);
            String monthKey = month.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
            summaryMap.put(monthKey, new TransactionsSummaryPerMonthDTO(monthKey, 0.0, 0.0, 0.0));
        }

        // Filtrar y procesar transacciones dentro de los últimos 12 meses
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getTransactionDate().toLocalDate();
            if (!transactionDate.isBefore(twelveMonthsAgo) && !transactionDate.isAfter(currentDate)) {
                String monthKey = transactionDate.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                TransactionsSummaryPerMonthDTO summaryDTO = summaryMap.get(monthKey);

                switch (transaction.getType()) {
                    case DEPOSIT:
                        summaryDTO.setDeposit(summaryDTO.getDeposit() + transaction.getAmount());
                        break;
                    case PAYMENT:
                        summaryDTO.setPayment(summaryDTO.getPayment() + transaction.getAmount());
                        break;
                    case INCOME:
                        summaryDTO.setIncome(summaryDTO.getIncome() + transaction.getAmount());
                        break;
                    default:
                        // Handle unexpected transaction type
                        break;
                }
            }
        }

        // Convertir el mapa a una lista ordenada por mes
        List<TransactionsSummaryPerMonthDTO> summaryList = new ArrayList<>(summaryMap.values());

        return summaryList;
    }


}