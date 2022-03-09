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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api")
@RestController
@Tag(name = "Product", description ="the API for managing Products")
public class ProductController {
	
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
	@Operation(summary = "used to print a message to the user",
			   description = "gets the current user from the principal and returns it in a message")
	@GetMapping("/user")
	public String getHello(Principal principal) {
		return "Hello " + principal.getName();
	}
	
	@Operation(summary = "updates the users password",
			   description = "gets the a user from the database by using their pricipal, sets the password to a value passed in by the user, then updates the original database entry")
	@PutMapping("/user")
	public ResponseEntity<User> updateUser(@RequestBody String password, Principal principal) throws ResourceNotFoundException {

		User user = userRepo.findByUsername(principal.getName()).orElse(new User());
		
		if( userRepo.existsById( user.getId() ) ) 
		{
			
			user.setPassword(password);
			User updated = userRepo.save(user);
			
			return ResponseEntity.status(200).body(updated);
		}
		
		throw new ResourceNotFoundException("User with id = " + user.getId() + " was not found");
	}
	
	
	//CRUD for sales
	@Operation(summary = "gets all the sales of the current user",
			   description = "uses the principle to determmine which user's database to retrieve, uses that userId to find their sales database, returns all items from sales database that matches")
	@GetMapping("/sales")
	public List<Sales> customerInfo(Principal principal) {
		
		User user = userRepo.findByUsername(principal.getName()).orElse(new User());
	
		return saleRepo.findByUserId(user.getId());
	}
	
	@Operation(summary = "adds a sales object to the sales_db",
			   description = "user passes in a product that want to add, uses the pricipal to determine the user, constructs a sales item by combining the product passed and info from the user_db, adds the new sales object to the sales_db")
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
	
	@Operation(summary = "removes a sales object from the sales_db",
			   description = "user passes in an id, checks if sales object exists on that id, checks if user is correct, deletes sales from database")
	@DeleteMapping("/sales/{id}")
	public ResponseEntity<Sales> deleteSales(@PathVariable int id, Principal principal) throws ResourceNotFoundException {
		
		if( saleRepo.existsById(id) ) {
			
			
			// get the book that will be deleted...
			Sales deleted = saleRepo.findById(id).get();
			
			if(principal.getName() == deleted.getUser())
			{
			// ...delete the book...
			saleRepo.deleteById(id);
			}
			else
			{
				throw new ResourceNotFoundException("Can't Find Matching Sales Object");
			}
			
			// ...return the book that was just deleted in the response
			return ResponseEntity.status(200).body(deleted);
		}
		
		throw new ResourceNotFoundException("Sales with id = " + id + " was not found");
	}
	
	
	//CRUD for products
	@Operation(summary = "retrieves all the products in database",
			   description = "recieves a list of products form the product_db and returns it to user")
	@GetMapping("/products")
	public List<Product> getAllProducts() {
	
	 return productRepo.findAll();
	}
	
	@Operation(summary = "creates a product in the database",
			   description = "user sends in a product from the body, it sets the id to null, then it creates it in the database")
	@PostMapping("/products")
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		
		product.setId(null);
		
		Product created = productRepo.save(product);
		
		return ResponseEntity.status(201).body(created);
	}
	
	@Operation(summary = "changes an item in the product database",
			   description = "takes in product information from the user, checks to see if the product exists, updates the product in the database")
	@PutMapping("/products")
	public ResponseEntity<Product> updateProduct(@RequestBody Product product) throws ResourceNotFoundException {

		if( productRepo.existsById( product.getId() ) ) {
			Product updated = productRepo.save(product);
			
			return ResponseEntity.status(200).body(updated);
		}
		
		throw new ResourceNotFoundException("Product with id = " + product.getId() + " was not found");
	}
	
	@Operation(summary = "removes a product from the database",
			   description = "user passes an id of product to be deleted, checks if that product exists, creates a temp product from that id, deletes the product from the database")
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
		
		throw new ResourceNotFoundException("Product with id = " + id + " was not found");
	}
	
	// a user will pass their credentials and get back a JWT
	// Once JWT is given to user, can use JWT for every other request, no need to provide credentials anymore
	@Operation(summary = "creates a token and saves it in Cookies",
			   description = "passes in a username a password from the body as well as a principal, if the principal already has data the user is already signed in so dont allow a 2nd login, authenticates the username and password entered, checks if the username and password is in database, creates a jwt, saves it in Cookies")
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
	
	@Operation(summary = "logout the user",
			   description = "removes the jwt from Cookies")
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














