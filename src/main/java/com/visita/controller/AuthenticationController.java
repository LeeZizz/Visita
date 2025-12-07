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
	ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest introSpectRequest)
			throws ParseException, JOSEException {
		var isAuthenticated = authenticationService.introspect(introSpectRequest);
		return ApiResponse.<IntrospectResponse>builder().result(isAuthenticated).build();
	}
}
