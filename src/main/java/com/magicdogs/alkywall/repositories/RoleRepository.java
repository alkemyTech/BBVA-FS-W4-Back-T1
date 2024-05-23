package com.magicdogs.alkywall.repositories;

import com.magicdogs.alkywall.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
