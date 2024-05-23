package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.entities.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

}
