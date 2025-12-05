package com.visita.security;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.visita.entities.AdminEntity;
import com.visita.repositories.AdminRepository;

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
	ApplicationRunner applicationRunner(AdminRepository adminRepository) {
		return args -> {
			if (adminRepository.findByUsername("admin").isEmpty()) {
				AdminEntity adminEntity = AdminEntity.builder().username("admin")
						.password(passwordEncoder.encode("admin")) // password: admin
						.fullName("Administrator").email("admin@example.com").build();
				adminRepository.save(adminEntity);
				log.warn("Admin user created with username: admin and password: admin");
			}
		};
	}
}
