package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findByUserIdUser(Long userId);
    Optional<Account> findByCbu(String cbu);
    Optional<Account> findByUserAndCurrency(User user, CurrencyType currency);
    Optional<List<Account>> findByUserEmail(String email);
    Optional<Account> findByIdAccountAndUser(Long id, User user);
}
