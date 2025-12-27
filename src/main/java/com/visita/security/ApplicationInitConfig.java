package com.visita.security;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.visita.entities.RoleEntity;
import com.visita.entities.UserEntity;
import com.visita.repositories.RoleRepository;
import com.visita.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

	PasswordEncoder passwordEncoder;

	public ApplicationInitConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
		return args -> {

			// 1. Initialize Roles
			if (!roleRepository.existsById("ADMIN")) {
				roleRepository.save(RoleEntity.builder().name("ADMIN").description("Administrator").build());
			}
			if (!roleRepository.existsById("USER")) {
				roleRepository.save(RoleEntity.builder().name("USER").description("Regular User").build());
			}
			if (!roleRepository.existsById("STAFF")) {
				roleRepository.save(RoleEntity.builder().name("STAFF").description("Staff Member").build());
			}

			// 2. Initialize Admin User
			if (userRepository.findByUsername("admin").isEmpty()) {
				RoleEntity adminRole = roleRepository.findById("ADMIN").get();
				Set<RoleEntity> roles = new HashSet<>();
				roles.add(adminRole);

				UserEntity adminUser = UserEntity.builder()
						.username("admin")
						.password(passwordEncoder.encode("admin")) // password: admin
						.fullName("Administrator")
						.email("admin@visita.com")
						.isActive(true)
						.roles(roles)
						.createdAt(LocalDateTime.now())
						.build();
				userRepository.save(adminUser);
				log.warn("Admin user created with username: admin and password: admin");
			}
		};
	}
}
