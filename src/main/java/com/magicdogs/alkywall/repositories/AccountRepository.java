package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    public Optional<List<Account>> findByUserId(Long userId);
}
