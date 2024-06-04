package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Page<Account>> findByUserIdUser(Long userId, Pageable pageable);
    Optional<Account> findByCbu(String cbu);
    Optional<Account> findByUserAndAccountTypeAndCurrency(User user, AccountType accountType, CurrencyType currency);
    Optional<List<Account>> findByUserEmail(String email);
    Optional<Account> findByIdAccountAndUser(Long id, User user);
    Boolean existsByAlias(String alias);
}
