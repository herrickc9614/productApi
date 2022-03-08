package com.cognixia.jump.model;

// model used to send in the username & password when a user first authenticates and needs to create a JWT
// using the /authenticate POST request
public class AuthenticationRequest {
	
	private String username;
	private String password;
	
	public AuthenticationRequest() {
		
	}

	public AuthenticationRequest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
