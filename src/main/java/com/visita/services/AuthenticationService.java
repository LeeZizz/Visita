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
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
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
					.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
			storedPassword = user.getPassword();
			subject = user.getEmail();
			scope = "USER";
		} else if (authenticationRequest.getUsername() != null && !authenticationRequest.getUsername().isEmpty()) {
			// ––– ADMIN FLOW –––
			var admin = adminRepository.findByUsername(authenticationRequest.getUsername())
					.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
			storedPassword = admin.getPassword();
			subject = admin.getUsername();
			scope = "ADMIN";
		} else {
			throw new WebException(ErrorCode.INVALID_USERNAME);
		}

		boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), storedPassword);
		if (!authenticated) {
			throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		var token = generateToken(subject, scope, 3600); // 1 hour
		var refreshToken = generateToken(subject, scope, 604800); // 7 days
		return AuthenticationResponse.builder().authenticated(true).token(token).refreshToken(refreshToken).build();
	}

	public AuthenticationResponse refreshToken(com.visita.dto.request.RefreshTokenRequest request)
			throws JOSEException, ParseException {
		var signedJWT = verifyToken(request.getToken());
		// var jit = signedJWT.getJWTClaimsSet().getJWTID();
		var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		// Check if expired is done in verifyToken mostly, but double check
		if (expiryTime.before(new Date()))
			throw new WebException(ErrorCode.UNAUTHENTICATED);

		var username = signedJWT.getJWTClaimsSet().getSubject();
		var scope = signedJWT.getJWTClaimsSet().getStringClaim("scope");

		var token = generateToken(username, scope, 3600);
		var refreshToken = generateToken(username, scope, 604800);

		return AuthenticationResponse.builder().authenticated(true).token(token).refreshToken(refreshToken).build();
	}

	public AuthenticationResponse outboundAuthenticate(com.visita.dto.request.ExchangeTokenRequest request) {
		var restTemplate = new org.springframework.web.client.RestTemplate();
		var httpHeaders = new org.springframework.http.HttpHeaders();
		httpHeaders.setBearerAuth(request.getToken());

		var httpEntity = new org.springframework.http.HttpEntity<>(httpHeaders);
		var response = restTemplate.exchange("https://www.googleapis.com/oauth2/v3/userinfo",
				org.springframework.http.HttpMethod.GET, httpEntity,
				com.visita.dto.response.OutboundUserResponse.class);

		var userInfo = response.getBody();
		if (userInfo == null)
			throw new WebException(ErrorCode.UNAUTHENTICATED);

		// Check if user exists
		var user = userRepository.findByEmail(userInfo.getEmail()).orElseGet(() -> {
			// Create new user
			var newUser = com.visita.entities.UserEntity.builder().email(userInfo.getEmail())
					.password(new BCryptPasswordEncoder().encode(java.util.UUID.randomUUID().toString())) // Random
																											// password
					.fullName(userInfo.getName()).build();
			return userRepository.save(newUser);
		});

		var token = generateToken(user.getEmail(), "USER", 3600);
		var refreshToken = generateToken(user.getEmail(), "USER", 604800);

		return AuthenticationResponse.builder().authenticated(true).token(token).refreshToken(refreshToken).build();
	}

	private String generateToken(String subject, String scope, long durationSeconds) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(subject).issuer("com.visita").issueTime(new Date())
				.expirationTime(new Date(new Date().getTime() + durationSeconds * 1000))
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

	private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		var verified = signedJWT.verify(verifier);

		if (!(verified && expirationTime.after(new Date())))
			throw new WebException(ErrorCode.UNAUTHENTICATED);

		return signedJWT;
	}
}
