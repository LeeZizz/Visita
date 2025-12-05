package com.visita.dto.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
    
    private String email;    // Used for Customers
    private String username; // Used for Admins
	private String password;
}
