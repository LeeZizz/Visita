package com.vitazi.services;

import java.text.ParseException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.vitazi.dto.request.AuthenticationRequest;
import com.vitazi.dto.request.IntrospectRequest;
import com.vitazi.dto.response.AuthenticationResponse;
import com.vitazi.dto.response.IntrospectResponse;
import com.vitazi.entities.UserEntity;
import com.vitazi.repositories.UserRepository;

@Service
public class AuthenticationService {

	@Value("${jwt.secret}")
	protected String secretKey;

	@Autowired
	private UserRepository userRepository;

	public IntrospectResponse introspect(IntrospectRequest introSpectRequest) throws JOSEException, ParseException {
//        var token = introSpectRequest.getToken();
//        boolean isValid = true;
//        try {
//            JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
//            SignedJWT signedJWT = SignedJWT.parse(token);
//            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//            var verified = signedJWT.verify(verifier);
//        } catch (JOSEException  | ParseException e) {
//            isValid = false;
//            throw new RuntimeException("Invalid SignedJWT token");
//        }
//        return IntrospectResponse.builder()
//                .valid(isValid ? "true" : "false")
//                .build();
		var token = introSpectRequest.getToken();
		JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		var verified = signedJWT.verify(verifier);
		return IntrospectResponse.builder().valid((verified && new Date().before(expirationTime)) ? "true" : "false")
				.build();

	}

	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		var user = userRepository.findByUserName((authenticationRequest.getUserName()))
				.orElseThrow(() -> new RuntimeException("User not found"));
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// return passwordEncoder.matches(authenticationRequest.getPassWord(),
		// user.getPassWord());

		boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassWord(), user.getPassWord());
		if (!authenticated)
			throw new RuntimeException("Not authenticated");

		var token = generateToken(user);
		System.out.println("Token: " + token);

		return AuthenticationResponse.builder().authenticated(true).token(token).build();
	}

	private String generateToken(UserEntity userEntity) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(userEntity.getUserName()).issuer("com.vitazi")
				.expirationTime(new Date(new Date().getTime() + 60 * 60 * 1000)) // 1 hour expiration
				.claim("scope", buildScope(userEntity)).build();

		Payload payload = new Payload(claimsSet.toJSONObject());
		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(secretKey));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			throw new RuntimeException(e);
		}
	}

	private String buildScope(UserEntity userEntity) {
		return userEntity.getRole().getRoleName();
	}
}
