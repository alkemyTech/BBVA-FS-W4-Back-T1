package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.entities.RoleNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleNameEnum roleName);
}
