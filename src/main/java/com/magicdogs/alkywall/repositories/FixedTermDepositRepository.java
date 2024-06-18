package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.FixedTermDeposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixedTermDepositRepository extends JpaRepository<FixedTermDeposit, Long> {
    Optional<Page<FixedTermDeposit>> findByAccount_UserIdUserOrderByClosingDateDesc(Long id, Pageable pageable);
}
