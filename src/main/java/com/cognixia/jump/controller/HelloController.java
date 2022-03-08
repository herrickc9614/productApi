package com.cognixia.jump.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.AuthenticationResponse;
import com.cognixia.jump.util.JwtUtil;

@RequestMapping("/api")
@RestController
public class HelloController {
	
	// manages and handles which users are valid
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtUtil jwtUtil;

	
	@GetMapping("/hello")
	public String getHello(Principal principal) {
		return "Hello " + principal.getName();
	}
	
	// a user will pass their credentials and get back a JWT
	// Once JWT is given to user, can use JWT for every other request, no need to provide credentials anymore
	@PostMapping("/login") 
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request, Principal principal) throws Exception {
		
		if(principal != null)
		{
			throw new Exception("Already Logged In");
		}
		
		// try to catch the exception for bad credentials, just so we can set our own message when this doesn't work
		try {
			// make sure we have a valid user by checking their username and password
			authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()) );
			
		} catch(BadCredentialsException e) {
			// provide our own message on why login didn't work
			throw new Exception("Incorrect username or password");
		}
		// as long as no exception was thrown, user is valid
		
		// load in the user details for that user
		final UserDetails userDetails = userDetailsService.loadUserByUsername( request.getUsername() );
		
		// generate the token for that user
		final String jwt = jwtUtil.generateTokens(userDetails);
		
		
		ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.domain("localhost")
				.build();
		
		// return the token
		return ResponseEntity.status(201).header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Login Successful");
	}
	
	@PostMapping("/logout") 
	public ResponseEntity<?> removeAuthenticationToken() throws Exception {	
		
		ResponseCookie cookie = ResponseCookie.from("jwt", null)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(0)
				.domain("localhost")
				.build();
		
		// return the token
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Logout Successful");
	}
	
}














