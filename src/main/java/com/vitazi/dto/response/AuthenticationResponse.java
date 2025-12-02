package com.vitazi.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {

	private boolean authenticated;
	private String token;
}
