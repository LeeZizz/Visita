package com.visita.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.visita.dto.request.AuthenticationRequest;
import com.visita.dto.request.IntrospectRequest;
import com.visita.dto.response.ApiResponse;
import com.visita.dto.response.AuthenticationResponse;
import com.visita.dto.response.IntrospectResponse;
import com.visita.services.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/login")
	ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
		var isAuthenticated = authenticationService.authenticate(authenticationRequest);
		return ApiResponse.<AuthenticationResponse>builder().result(isAuthenticated).build();
	}

	@PostMapping("/introspect")
	ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspectRequest)
			throws ParseException, JOSEException {
		var isAuthenticated = authenticationService.introspect(introspectRequest);
		return ApiResponse.<IntrospectResponse>builder().result(isAuthenticated).build();
	}

	@PostMapping("/refresh")
	ApiResponse<AuthenticationResponse> refresh(@RequestBody com.visita.dto.request.RefreshTokenRequest request)
			throws ParseException, JOSEException {
		var result = authenticationService.refreshToken(request);
		return ApiResponse.<AuthenticationResponse>builder().result(result).build();
	}

	@PostMapping("/outbound/authentication")
	ApiResponse<AuthenticationResponse> outboundAuthenticate(
			@RequestBody com.visita.dto.request.ExchangeTokenRequest request) {
		var result = authenticationService.outboundAuthenticate(request);
		return ApiResponse.<AuthenticationResponse>builder().result(result).build();
	}

	@PostMapping("/logout")
	ApiResponse<Void> logout(@RequestBody com.visita.dto.request.LogoutRequest request)
			throws ParseException, JOSEException {
		authenticationService.logout(request);
		return ApiResponse.<Void>builder().build();
	}
}
