package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.ThirdAccount;
import com.magicdogs.alkywall.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThirdAccountRepository extends JpaRepository<ThirdAccount, Long> {
    List<ThirdAccount> findByUser(User user);
    Optional<ThirdAccount> findByDestinationAccountAndUser(Account account, User user);
    Optional<ThirdAccount> findByIdThirdAccountAndUser(Long idThirdAccount, User user);
}
