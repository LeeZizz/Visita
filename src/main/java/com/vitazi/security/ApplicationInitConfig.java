package com.vitazi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vitazi.entities.Gender;
import com.vitazi.entities.RoleEntity;
import com.vitazi.entities.UserEntity;
import com.vitazi.repositories.RoleRepository;
import com.vitazi.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository) {
		return args -> {
			if (userRepository.findByUserName("admin").isEmpty()) {
//                RoleEntity roleAdmin = roleRepository.findByRoleName("ADMIN");
//                roleAdmin.setRoleName("ADMIN");
//                roleRepository.save(roleAdmin);
				RoleEntity roleAdmin = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
					RoleEntity newRole = new RoleEntity();
					newRole.setRoleName("ADMIN");
					return roleRepository.save(newRole);
				});

				UserEntity userEntity = UserEntity.builder().userName("admin")
						.passWord(passwordEncoder.encode("admin123")) // password: admin123
						.fullName("Administrator").email("admin@gmail.com").gender(Gender.valueOf("MALE"))
						.phone("12345678910").role(roleAdmin).build();
				userRepository.save(userEntity);
				log.warn("Admin user created with username: admin and password: admin123");
			}
		};
	}
}
