package com.visita.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.visita.entities.RoleEntity;
import com.visita.entities.UserEntity;
import com.visita.repositories.RoleRepository;
import com.visita.repositories.UserRepository;

/**
 * Initializes default roles and users for development environment.
 * Only runs when 'dev' profile is active.
 * Skips creation if data already exists.
 */
@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
        initUsers();
    }

    private void initRoles() {
        createRoleIfNotExists("ADMIN", "Administrator");
        createRoleIfNotExists("STAFF", "Staff");
        createRoleIfNotExists("USER", "User");
    }

    private void createRoleIfNotExists(String name, String description) {
        if (roleRepository.existsById(name)) {
            log.debug("Role '{}' already exists, skipping", name);
            return;
        }

        RoleEntity role = RoleEntity.builder()
                .name(name)
                .description(description)
                .build();
        roleRepository.save(role);
        log.info("Created role: {}", name);
    }

    private void initUsers() {
        // Admin: login via username 'admin', password 'admin123'
        createUserIfNotExists(
                "admin@visita.com",
                "admin",
                "admin123",
                "System Administrator",
                "ADMIN");

        // Staff: login via username 'staff', password 'staff123'
        createUserIfNotExists(
                "staff@visita.com",
                "staff",
                "staff123",
                "Staff Member",
                "STAFF");

        // User: login via email, password 'user123'
        createUserIfNotExists(
                "user@visita.com",
                null,
                "user123",
                "Test User",
                "USER");
    }

    private void createUserIfNotExists(String email, String username, String password,
            String fullName, String roleName) {
        // Skip if user already exists (by email or username)
        if (userRepository.existsByEmail(email)) {
            log.debug("User with email '{}' already exists, skipping", email);
            return;
        }
        if (username != null && userRepository.existsByUsername(username)) {
            log.debug("User with username '{}' already exists, skipping", username);
            return;
        }

        RoleEntity role = roleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        UserEntity user = UserEntity.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .isActive(true)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
        log.info("Created user: {} ({})", email, roleName);
    }
}
