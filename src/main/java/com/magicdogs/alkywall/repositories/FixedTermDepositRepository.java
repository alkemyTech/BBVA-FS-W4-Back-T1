package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.FixedTermDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedTermDepositRepository extends JpaRepository<FixedTermDeposit, Long> {

}
