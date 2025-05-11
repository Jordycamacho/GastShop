package com.example.bordados.config;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.bordados.model.Notices;
import com.example.bordados.model.Permission;
import com.example.bordados.model.Role;
import com.example.bordados.model.User;
import com.example.bordados.model.UserType;
import com.example.bordados.model.Enums.RoleEnum;
import com.example.bordados.repository.NoticesRepository;
import com.example.bordados.repository.PermissionRepository;
import com.example.bordados.repository.RoleRepository;
import com.example.bordados.repository.UserRepository;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NoticesRepository noticesRepository;


    @Override
    public void run(String... args) throws Exception {
        initializeRolesAndPermissions();
        createAdminUser();
        initializeDefaultNotices();
    }

    private void initializeDefaultNotices() {
        if (noticesRepository.count() == 0) {
            Notices defaultNotices = Notices.builder()
                .offers("¡Obtén un 10% de descuento en tu primer pedido!")
                .bannerMain("Nueva colección primavera/verano 2025")
                .bannerSecondary("¡Descuentos en conjuntos!")
                .build();
            
            noticesRepository.save(defaultNotices);
        }
    }

    private void initializeRolesAndPermissions() {
        Permission readPermission = getOrCreatePermission("READ");
        Permission writePermission = getOrCreatePermission("WRITE");
        Permission adminPermission = getOrCreatePermission("ADMIN");

        createRoleIfNotExists(RoleEnum.ADMIN, Set.of(readPermission, writePermission, adminPermission));
        createRoleIfNotExists(RoleEnum.USER, Set.of(readPermission));
        createRoleIfNotExists(RoleEnum.INVITED, Collections.emptySet());
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    return permissionRepository.save(permission);
                });
    }

    private void createRoleIfNotExists(RoleEnum roleEnum, Set<Permission> permissions) {
        if (roleRepository.findByRoleEnum(roleEnum).isEmpty()) {
            Role role = new Role();
            role.setRoleEnum(roleEnum);
            role.setPermission(permissions);
            roleRepository.save(role);
        }
    }

    private void createAdminUser() {
        if (userRepository.findUserByEmail("gastboutique@gmail.com").isEmpty()) {
            Role adminRole = roleRepository.findByRoleEnum(RoleEnum.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("Admin role not found. Did initialization fail?"));

            User adminUser = new User();
            adminUser.setName("Gast Shop");
            adminUser.setEmail("gastboutique@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("Gast012890."));
            adminUser.setRegistrationDate(new Date());
            adminUser.setAddress("narnia");
            adminUser.setType(UserType.ROLE_ADMIN);
            adminUser.setAffiliateCode("ADMIN001");
            adminUser.setEnabled(true);
            adminUser.setAccountNoExpired(true);
            adminUser.setAccountNoLocked(true);
            adminUser.setCredentialNoExpired(true);
            adminUser.setRoles(Set.of(adminRole));

            User adminUserOther = new User();
            adminUserOther.setName("Gast Shop");
            adminUserOther.setEmail("jordycamacho225@gmail.com");
            adminUserOther.setPassword(passwordEncoder.encode("Jordy012890."));
            adminUserOther.setRegistrationDate(new Date());
            adminUserOther.setAddress("narnia");
            adminUserOther.setType(UserType.ROLE_ADMIN);
            adminUserOther.setAffiliateCode("ADMIN002");
            adminUserOther.setEnabled(true);
            adminUserOther.setAccountNoExpired(true);
            adminUserOther.setAccountNoLocked(true);
            adminUserOther.setCredentialNoExpired(true);
            adminUserOther.setRoles(Set.of(adminRole));
            
            userRepository.save(adminUser);
        }
    }
}