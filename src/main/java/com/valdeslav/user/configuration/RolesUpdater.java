package com.valdeslav.user.configuration;

import com.valdeslav.user.model.Role;
import com.valdeslav.user.model.enums.Roles;
import com.valdeslav.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class RolesUpdater implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Map<String, Role> roles = new HashMap<>();
        for (Role role : roleRepository.findAll()) {
            roles.put(role.getName(), role);
        }
        for (Roles roleEnum : Roles.values()) {
            roles.putIfAbsent(roleEnum.name(), new Role(roleEnum.name()));
        }

        roleRepository.saveAll(roles.values());
    }
}
