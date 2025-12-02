package com.vitazi.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.vitazi.dto.request.AuthenticationRequest;
import com.vitazi.dto.request.IntrospectRequest;
import com.vitazi.dto.response.ApiResponse;
import com.vitazi.dto.response.AuthenticationResponse;
import com.vitazi.dto.response.IntrospectResponse;
import com.vitazi.services.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

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
