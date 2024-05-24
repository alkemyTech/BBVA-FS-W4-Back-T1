package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail( String password);
}
