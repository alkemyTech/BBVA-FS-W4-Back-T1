package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Page<Account>> findByUserIdUser(Long userId, Pageable pageable);
    Optional<Account> findByCbu(String cbu);
    Optional<Account> findByUserAndCurrency(User user, CurrencyType currency);
    Optional<List<Account>> findByUserEmail(String email);
    Optional<Account> findByIdAccountAndUser(Long id, User user);
    Boolean existsByAlias(String alias);
}
