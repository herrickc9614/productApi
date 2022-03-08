package com.cognixia.jump.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cognixia.jump.filter.JwtRequestFilter;


@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtRequestFilter jwtRequestFilter;

	// AUTHENTICATE
	@Override
	protected void configure( AuthenticationManagerBuilder auth ) throws Exception {
		
		// only look to load the one in-memory user we have (user1)
		auth.userDetailsService(userDetailsService);
		
	}
	
	// AUTHORIZATION
	@Override
	protected void configure( HttpSecurity http ) throws Exception {

//		http.csrf().disable()
//			.authorizeRequests()
//			.antMatchers("/api/authenticate").permitAll() // permit anyone to create a token as long as they're valid user
//			.anyRequest().authenticated(); // any request to any of our APIs needs to be authenticated (token or user info)
		
		http.csrf().disable()
			.authorizeRequests()
			.antMatchers("/api/login").permitAll() // permit anyone to create a token as long as they're valid user
			.anyRequest().authenticated()
			.and().sessionManagement()
				.sessionCreationPolicy( SessionCreationPolicy.STATELESS ); // tell spring security to not create any sessions, we want to be
																		   // stateless b/c we're using jwts
		
		// make sure jwt filter is checked first before any other filter, especially before the filter that checks for the correct
		// username and password of a user
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		
	}
	
	// mainly used to DECODE passwords
	@Bean
	public PasswordEncoder passwordEncoder() {
		
		// create no password encode, old way of setting it up, still works just depreciated 
		return NoOpPasswordEncoder.getInstance();
		
	}
	
	// method will provid spring security an object (AuthenticationManager) that can be used to authenticate
	// users accessing the APIs in our service
	// also mark it with @Bean so it can be loaded from the Spring Context and we can have dependency injection within HelloController
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		
		return super.authenticationManagerBean();
	}
	
}















