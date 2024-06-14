package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Optional<Page<Transaction>> findByAccountUserIdUser(Long id, Pageable pageable);

    Optional<Page<Transaction>> findByAccountIdAccount(Long id, Pageable pageable);
}
