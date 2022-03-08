package com.cognixia.jump.model;

// response object for when /authenticate POST request is called and returns this JWT
public class AuthenticationResponse {
	
	private final String jwt;
	
	public AuthenticationResponse(String jwt) {
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}

}
