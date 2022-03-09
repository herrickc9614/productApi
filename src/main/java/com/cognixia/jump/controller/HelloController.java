package com.cognixia.jump.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.AuthenticationResponse;
import com.cognixia.jump.model.Product;
import com.cognixia.jump.model.Sales;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.ProductRepository;
import com.cognixia.jump.repository.SalesRepository;
import com.cognixia.jump.repository.UserRepository;
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
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	SalesRepository saleRepo;
	
	@Autowired
	ProductRepository productRepo;
	
	
	//CRUD for User
	@GetMapping("/user")
	public String getHello(Principal principal) {
		return "Hello " + principal.getName();
	}
	
	@PutMapping("/user")
	public ResponseEntity<User> updateUser(@RequestBody String password, Principal principal) throws ResourceNotFoundException {

		User user = userRepo.findByUsername(principal.getName()).orElse(new User());
		
		if( userRepo.existsById( user.getId() ) ) 
		{
			
			user.setPassword(password);
			User updated = userRepo.save(user);
			
			return ResponseEntity.status(200).body(updated);
		}
		
		throw new ResourceNotFoundException("Product with id = " + user.getId() + " was not found");
	}
	
	
	//CRUD for sales
	@GetMapping("/sales")
	public List<Sales> customerInfo(Principal principal) {
		
		User user = userRepo.findByUsername(principal.getName()).orElse(new User());
	
		return saleRepo.findByUserId(user.getId());
	}
	
	@PostMapping("/sales")
	public ResponseEntity<Sales> createSales(@RequestBody Product product, Principal principal) throws ResourceNotFoundException {
		
		Sales sales = new Sales();
		User user = userRepo.findByUsername(principal.getName()).orElse(new User());
		
		if( productRepo.existsById( product.getId() ) ) 
		{
			sales.setId(null);
			sales.setUser(user);
			sales.setProduct(product);
			sales.setTotalProduct(2);
			sales.setTotalAmount(sales.getTotalProduct() * product.getProductCost());
		
			Sales created = saleRepo.save(sales);
		
			return ResponseEntity.status(201).body(created);
		}
		
		throw new ResourceNotFoundException("Product with id = " + product.getId() + " was not found");	
	}
	
	@DeleteMapping("/sales/{id}")
	public ResponseEntity<Sales> deleteSales(@PathVariable int id) throws ResourceNotFoundException {
		
		if( saleRepo.existsById(id) ) {
			
			// get the book that will be deleted...
			Sales deleted = saleRepo.findById(id).get();
			
			// ...delete the book...
			saleRepo.deleteById(id);
			
			// ...return the book that was just deleted in the response
			return ResponseEntity.status(200).body(deleted);
		}
		
		throw new ResourceNotFoundException("Book with id = " + id + " was not found");
	}
	
	
	//CRUD for products
	@GetMapping("/products")
	public List<Product> getAllProducts() {
	
	 return productRepo.findAll();
	}
	
	@PostMapping("/products")
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		
		product.setId(null);
		
		Product created = productRepo.save(product);
		
		return ResponseEntity.status(201).body(created);
	}
	
	@PutMapping("/products")
	public ResponseEntity<Product> updateProduct(@RequestBody Product product) throws ResourceNotFoundException {

		if( productRepo.existsById( product.getId() ) ) {
			Product updated = productRepo.save(product);
			
			return ResponseEntity.status(200).body(updated);
		}
		
		throw new ResourceNotFoundException("Product with id = " + product.getId() + " was not found");
	}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Product> deleteProduct(@PathVariable int id) throws ResourceNotFoundException {
		
		if( productRepo.existsById(id) ) {
			
			// get the book that will be deleted...
			Product deleted = productRepo.findById(id).get();
			
			// ...delete the book...
			productRepo.deleteById(id);
			
			// ...return the book that was just deleted in the response
			return ResponseEntity.status(200).body(deleted);
		}
		
		throw new ResourceNotFoundException("Book with id = " + id + " was not found");
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














