package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {


}
