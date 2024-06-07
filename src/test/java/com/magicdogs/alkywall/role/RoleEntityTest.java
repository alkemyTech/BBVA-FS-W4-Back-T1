package com.magicdogs.alkywall.role;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.enums.RoleNameEnum;

public class RoleEntityTest {

    @Test
    @DisplayName("Crear rol con campos obligatorios")
    public void testCreateRoleWithMandatoryFields() {
        Role role = new Role();
        role.setName(RoleNameEnum.ADMIN);

        assertEquals(RoleNameEnum.ADMIN, role.getName());
        assertNull(role.getDescription());
    }

    @Test
    @DisplayName("Crear rol con descripción opcional")
    public void testCreateRoleWithOptionalDescription() {
        Role role = new Role();
        role.setName(RoleNameEnum.USER);
        role.setDescription("This is a user role");

        assertEquals(RoleNameEnum.USER, role.getName());
        assertEquals("This is a user role", role.getDescription());
    }

    @Test
    @DisplayName("Generación de timestamps al crear un rol")
    public void testTimestampsOnRoleCreation() {
        Role role = new Role();
        role.setName(RoleNameEnum.ADMIN);
        role.onCreate();

        assertNotNull(role.getCreationDate());
        assertNotNull(role.getUpdateDate());
        assertEquals(role.getCreationDate(), role.getUpdateDate());
    }

    @Test
    @DisplayName("Generación de timestamps al actualizar un rol")
    public void testTimestampsOnRoleUpdate() {
        Role role = new Role();
        role.setName(RoleNameEnum.USER);
        role.onCreate();

        LocalDateTime creationDate = role.getCreationDate();
        LocalDateTime updateDate = role.getUpdateDate();

        // Simular el paso del tiempo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        role.onUpdate();

        assertEquals(creationDate, role.getCreationDate());
        assertNotEquals(updateDate, role.getUpdateDate());
        assertTrue(role.getUpdateDate().isAfter(updateDate));
    }

    @Test
    @DisplayName("Validar enum RoleNameEnum")
    public void testRoleNameEnum() {
        Role role = new Role();

        role.setName(RoleNameEnum.ADMIN);
        assertEquals(RoleNameEnum.ADMIN, role.getName());

        role.setName(RoleNameEnum.USER);
        assertEquals(RoleNameEnum.USER, role.getName());

        // Asegurar que solo ADMIN y USER estan permitidos
        assertThrows(IllegalArgumentException.class, () -> RoleNameEnum.valueOf("INVALID_ROLE"));
    }
}
