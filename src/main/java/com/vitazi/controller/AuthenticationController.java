package com.vitazi.controller;

import com.nimbusds.jose.JOSEException;
import com.vitazi.dto.request.AuthenticationRequest;
import com.vitazi.dto.request.IntrospectRequest;
import com.vitazi.dto.response.ApiResponse;
import com.vitazi.dto.response.AuthenticationResponse;
import com.vitazi.dto.response.IntrospectResponse;
import com.vitazi.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse <AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        var isAuthenticated = authenticationService.authenticate(authenticationRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(isAuthenticated)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse <IntrospectResponse> authenticate(@RequestBody IntrospectRequest introSpectRequest) throws ParseException, JOSEException {
        var isAuthenticated = authenticationService.introspect(introSpectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(isAuthenticated)
                .build();
    }
    @GetMapping("/home")
    public String home() {
        return "Welcome to Booktour OAuth Service";
    }

}
