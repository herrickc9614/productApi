package com.cognixia.jump.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserRepository repo;

	// instead of loading in a user from a database, we will only ever have one user that we will check for
	// have a single hard coded user
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//repo.findByUsername(username);
		User user = repo.findByUsername(username).orElse(new User());
		
		if(user.equals(null)) {	
			throw new UsernameNotFoundException(username);
		}
		
		MyUserDetails help = new MyUserDetails(user);
		
		// User -> security packages and it's a default model object you can use that also implements UserDetails
		// User has username of user1 and password of pw123 and no roles
		return help;
	}

}
