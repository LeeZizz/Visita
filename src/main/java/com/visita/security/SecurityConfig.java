package com.visita.security;

import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final String[] PUBLIC_POST_API = { "/users/create", "/auth/login", "/auth/introspect", "/auth/refresh",
			"/auth/outbound/authentication", "/auth/logout", "/api/payment/ipn-momo" };

	private final String[] PUBLIC_GET_API = { "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
			"/tours/**" };
	@Value("${jwt.secret}")
	protected String signedKey;

	@org.springframework.beans.factory.annotation.Autowired
	private com.visita.repositories.InvalidatedTokenRepository invalidatedTokenRepository;

	@org.springframework.beans.factory.annotation.Autowired
	private com.visita.config.FrontendConfig frontendConfig;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(request -> request
				.requestMatchers(HttpMethod.POST, PUBLIC_POST_API).permitAll()
				.requestMatchers(HttpMethod.GET, PUBLIC_GET_API).permitAll()
				.requestMatchers("/admins/**").hasRole("ADMIN")
				.requestMatchers("/staffs/**").hasAnyRole("STAFF", "ADMIN")
				.anyRequest().authenticated());
		httpSecurity.oauth2ResourceServer((oauth2) -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
				.jwtAuthenticationConverter(jwtAuthenticationConverter()))
				.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		return httpSecurity.build();
	}

	@Bean
	org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
		org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("");

		org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(frontendConfig.getAllowedOriginsList());
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	JwtDecoder jwtDecoder() {
		SecretKeySpec secretKeySpec = new SecretKeySpec(signedKey.getBytes(), "HS256");
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS256)
				.build();

		org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> withClockSkew = new org.springframework.security.oauth2.jwt.JwtTimestampValidator();
		org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> withBlacklist = new org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt>() {
			@Override
			public org.springframework.security.oauth2.core.OAuth2TokenValidatorResult validate(
					org.springframework.security.oauth2.jwt.Jwt token) {
				if (invalidatedTokenRepository.existsById(token.getId())) {
					return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
							.failure(new org.springframework.security.oauth2.core.OAuth2Error("invalid_token",
									"Token has been invalidated", null));
				}
				return org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success();
			}
		};

		org.springframework.security.oauth2.core.OAuth2TokenValidator<org.springframework.security.oauth2.jwt.Jwt> validator = new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
				withClockSkew, withBlacklist);

		jwtDecoder.setJwtValidator(validator);

		return jwtDecoder;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}
}
