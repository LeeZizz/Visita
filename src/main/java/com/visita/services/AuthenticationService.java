package com.visita.services;

import java.text.ParseException;
import java.util.Date;

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
import com.visita.entities.UserEntity;
import com.visita.repositories.UserRepository;

@Service
public class AuthenticationService {

	private final UserRepository userRepository;
	@Value("${jwt.secret}")
	protected String secretKey;

	public AuthenticationService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public IntrospectResponse introspect(IntrospectRequest introSpectRequest) throws JOSEException, ParseException {
		var token = introSpectRequest.getToken();
		JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		var verified = signedJWT.verify(verifier);
		return IntrospectResponse.builder().valid((verified && new Date().before(expirationTime)) ? "true" : "false")
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		var user = userRepository.findByEmail((authenticationRequest.getEmail()))
				.orElseThrow(() -> new RuntimeException("User not found"));
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
		if (!authenticated)
			throw new RuntimeException("Not authenticated");

		var token = generateToken(user);
		System.out.println("Token: " + token);

		return AuthenticationResponse.builder().authenticated(true).token(token).build();
	}

	private String generateToken(UserEntity userEntity) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userEntity.getEmail()).issuer("com.visita")
				.expirationTime(new Date(new Date().getTime() + 60 * 60 * 1000)) // 1 hour expiration
				.claim("scope", buildScope()).build();

		Payload payload = new Payload(claimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(secretKey.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
	}

	private String buildScope() {
		// UserEntity does not have a role. Returning a default scope.
		return "USER";
	}
}
