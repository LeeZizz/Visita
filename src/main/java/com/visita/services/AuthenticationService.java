package com.visita.services;

import java.text.ParseException;
import java.util.Date;

import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.visita.dto.request.AuthenticationRequest;
import com.visita.dto.request.IntrospectRequest;
import com.visita.dto.response.AuthenticationResponse;
import com.visita.dto.response.IntrospectResponse;
import com.visita.repositories.AdminRepository;
import com.visita.repositories.UserRepository;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;
	private final AdminRepository adminRepository;

	@Value("${jwt.secret}")
	protected String secretKey;

	public AuthenticationService(UserRepository userRepository, AdminRepository adminRepository) {
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
	}

	public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
		var token = introspectRequest.getToken();
		JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		var verified = signedJWT.verify(verifier);
		return IntrospectResponse.builder().valid((verified && new Date().before(expirationTime)) ? "true" : "false")
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String subject;
		String scope;
		String storedPassword;

		if (authenticationRequest.getEmail() != null && !authenticationRequest.getEmail().isEmpty()) {
			// ––– USER FLOW –––
			var user = userRepository.findByEmail(authenticationRequest.getEmail())
					.orElseThrow(() -> new RuntimeException("User not found"));
			storedPassword = user.getPassword();
			subject = user.getEmail();
			scope = "USER";
		} else if (authenticationRequest.getUsername() != null && !authenticationRequest.getUsername().isEmpty()) {
			// ––– ADMIN FLOW –––
			var admin = adminRepository.findByUsername(authenticationRequest.getUsername())
					.orElseThrow(() -> new RuntimeException("Admin not found"));
			storedPassword = admin.getPassword();
			subject = admin.getUsername();
			scope = "ADMIN";
		} else {
			throw new RuntimeException("Invalid login request: must provide email or username");
		}

		boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), storedPassword);
		if (!authenticated) {
//			throw new RuntimeException("Not authenticated");
            throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		var token = generateToken(subject, scope);
		return AuthenticationResponse.builder().authenticated(true).token(token).build();
	}

	private String generateToken(String subject, String scope) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(subject).issuer("com.visita").issueTime(new Date())
				.expirationTime(new Date(new Date().getTime() + 60 * 60 * 1000)) // 1 hour expiration
				.claim("scope", scope).build();

		Payload payload = new Payload(claimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(secretKey.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
	}
}
