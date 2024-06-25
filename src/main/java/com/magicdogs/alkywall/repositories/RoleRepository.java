package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.enums.RoleNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleNameEnum rolename);
}
