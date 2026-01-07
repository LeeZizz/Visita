package com.visita.services;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

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
import com.visita.entities.RoleEntity;
import com.visita.entities.UserEntity;
import com.visita.exceptions.ErrorCode;
import com.visita.exceptions.WebException;
import com.visita.repositories.UserRepository;
import com.visita.repositories.RoleRepository;

@Service
public class AuthenticationService {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final com.visita.repositories.InvalidatedTokenRepository invalidatedTokenRepository;
	private final com.visita.repositories.RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${jwt.secret}")
	protected String secretKey;

	public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository,
			com.visita.repositories.InvalidatedTokenRepository invalidatedTokenRepository,
			com.visita.repositories.RefreshTokenRepository refreshTokenRepository,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.invalidatedTokenRepository = invalidatedTokenRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
		var token = request.getToken();
		boolean isValid = true;
		try {
			verifyToken(token, false);
		} catch (WebException | JOSEException | ParseException e) {
			isValid = false;
		}

		return IntrospectResponse.builder().valid(isValid ? "true" : "false").build();
	}

	@Transactional
	public void logout(com.visita.dto.request.LogoutRequest request) throws ParseException, JOSEException {
		try {
			var signToken = verifyToken(request.getToken(), true);

			String jit = signToken.getJWTClaimsSet().getJWTID();
			Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

			// Blacklist the Access Token
			com.visita.entities.InvalidatedToken invalidatedToken = com.visita.entities.InvalidatedToken.builder()
					.id(jit).expiryTime(expiryTime).build();
			invalidatedTokenRepository.save(invalidatedToken);

			// Attempt to find user and revoke refresh tokens for security

			String username = signToken.getJWTClaimsSet().getSubject();
			userRepository.findByUsername(username).or(() -> userRepository.findByEmail(username)).ifPresent(user -> {
				refreshTokenRepository.deleteByUser(user);
			});

		} catch (WebException exception) {
			log.info("Token already expired or invalid");
		}
	}

	public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
		UserEntity user = null;

		if (StringUtils.hasText(authenticationRequest.getEmail())) {
			user = userRepository.findByEmail(authenticationRequest.getEmail())
					.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		} else if (StringUtils.hasText(authenticationRequest.getUsername())) {
			user = userRepository.findByUsername(authenticationRequest.getUsername())
					.orElseThrow(() -> new WebException(ErrorCode.USER_NOT_FOUND));
		} else {
			throw new WebException(ErrorCode.USER_NOT_FOUND);
		}

		if (!user.getIsActive()) {
			throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
		if (!authenticated) {
			throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		var token = generateToken(user, 3600); // 1 hour
		var refreshToken = generateToken(user, 604800); // 7 days

		// Save Refresh Token to DB
		saveRefreshToken(refreshToken, user);

		return AuthenticationResponse.builder().authenticated(true).token(token).refreshToken(refreshToken).build();
	}

	public AuthenticationResponse refreshToken(com.visita.dto.request.RefreshTokenRequest request)
			throws JOSEException, ParseException {
		// 1. Verify signature of the passed Refresh Token
		var signedJWT = verifyToken(request.getToken(), true);
		var jti = signedJWT.getJWTClaimsSet().getJWTID();

		// 2. Check if exists in DB
		var storedToken = refreshTokenRepository.findById(jti)
				.orElseThrow(() -> new WebException(ErrorCode.UNAUTHENTICATED));

		// 3. Check Expiry (Double check, although verifyToken checks JWT exp)
		if (storedToken.getExpiryDate().before(new Date())) {
			refreshTokenRepository.delete(storedToken);
			throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		// 4. Revoke (Delete) Old Token (Rotation)
		refreshTokenRepository.delete(storedToken);

		var username = signedJWT.getJWTClaimsSet().getSubject();
		var user = userRepository.findByUsername(username)
				.or(() -> userRepository.findByEmail(username))
				.orElseThrow(() -> new WebException(ErrorCode.UNAUTHENTICATED));

		// Check if user is deactivated
		if (!user.getIsActive()) {
			throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		// 5. Generate New Tokens
		var token = generateToken(user, 3600);
		var refreshToken = generateToken(user, 604800);

		// 6. Save New Refresh Token
		saveRefreshToken(refreshToken, user);

		return AuthenticationResponse.builder().authenticated(true).token(token).refreshToken(refreshToken).build();
	}

	public AuthenticationResponse outboundAuthenticate(com.visita.dto.request.ExchangeTokenRequest request) {
		org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
		org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
		headers.set("Authorization", "Bearer " + request.getToken());

		org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>("", headers);
		org.springframework.http.ResponseEntity<com.visita.dto.response.OutboundUserResponse> response = restTemplate
				.exchange("https://www.googleapis.com/oauth2/v3/userinfo", HttpMethod.GET, entity,
						com.visita.dto.response.OutboundUserResponse.class);

		var userInfo = response.getBody();
		if (userInfo == null)
			throw new WebException(ErrorCode.UNAUTHENTICATED);

		String email = userInfo.getEmail();

		var user = userRepository.findByEmail(email).orElseGet(() -> {
			RoleEntity userRole = roleRepository.findById("USER").orElseGet(() -> {
				return RoleEntity.builder().name("USER").description("User role").build();
			});

			var roles = new HashSet<RoleEntity>();
			roles.add(userRole);

			var newUser = com.visita.entities.UserEntity.builder().email(userInfo.getEmail())
					.password(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
					.fullName(userInfo.getName())
					.isActive(true)
					.roles(roles)
					.build();
			return userRepository.save(newUser);
		});

		// Check if existing user is deactivated
		if (!user.getIsActive()) {
			throw new WebException(ErrorCode.UNAUTHENTICATED);
		}

		var token = generateToken(user, 3600);
		var refreshToken = generateToken(user, 604800);

		saveRefreshToken(refreshToken, user);

		return AuthenticationResponse.builder().authenticated(true).token(token).refreshToken(refreshToken).build();
	}

	private void saveRefreshToken(String token, UserEntity user) {
		try {
			SignedJWT signedJWT = SignedJWT.parse(token);
			String jti = signedJWT.getJWTClaimsSet().getJWTID();
			Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

			com.visita.entities.RefreshTokenEntity tokenEntity = com.visita.entities.RefreshTokenEntity.builder()
					.id(jti)
					.token(token)
					.expiryDate(expiryTime)
					.user(user)
					.build();
			refreshTokenRepository.save(tokenEntity);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateToken(UserEntity user, long durationSeconds) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

		StringJoiner stringJoiner = new StringJoiner(" ");
		if (user.getRoles() != null) {
			user.getRoles().forEach(role -> stringJoiner.add("ROLE_" + role.getName()));
		}

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUsername() != null ? user.getUsername() : user.getEmail())
				.issuer("visita.com")
				.issueTime(new Date())
				.expirationTime(new Date(new Date().getTime() + durationSeconds * 1000L))
				.jwtID(java.util.UUID.randomUUID().toString())
				.claim("scope", stringJoiner.toString())
				.build();

		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(secretKey.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			log.error("Cannot create token", e);
			throw new RuntimeException(e);
		}
	}

	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		JWSVerifier verifier = new MACVerifier(secretKey.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);

		Date expiryTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
						.plus(signedJWT.getJWTClaimsSet().getClaim("refresh_expiration") == null ? 604800
								: (long) signedJWT.getJWTClaimsSet().getClaim("refresh_expiration"),
								java.time.temporal.ChronoUnit.SECONDS)
						.toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier);

		if (!(verified && expiryTime.after(new Date())))
			throw new WebException(ErrorCode.UNAUTHENTICATED);

		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
			throw new WebException(ErrorCode.UNAUTHENTICATED);

		return signedJWT;
	}
}
