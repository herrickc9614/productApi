package com.cognixia.jump.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cognixia.jump.exception.ResourceNotFoundException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.Car;
import com.cognixia.jump.model.Product;
import com.cognixia.jump.model.Sales;
import com.cognixia.jump.model.User;
import com.cognixia.jump.model.User.Role;
import com.cognixia.jump.repository.ProductRepository;
import com.cognixia.jump.repository.SalesRepository;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HelloController.class)
public class HelloControllerTest {

	
	private final String STARTING_URI = "http://localhost:8080/api";
	
	// mocks the http request/response
	@Autowired
	private MockMvc mvc;
	
	// when methods from the service are called within the controller
	// we want to decide what data gets returned from those methods instead of actually
	// running that method
	
	// manages and handles which users are valid
	@MockBean
	AuthenticationManager authenticationManager;
	
	@MockBean
	UserDetailsService userDetailsService;
	
	@MockBean
	JwtUtil jwtUtil;
	
	@MockBean
	UserRepository userRepo;
	
	@MockBean
	SalesRepository saleRepo;
	
	@MockBean
	ProductRepository productRepo;
	
	
	// when controller tries to autowire the service, mock the creation of the service,
	// don't actually create a proper service object (mock service object created instead)
	@InjectMocks
	private HelloController controller;
		
	@Test
	@WithMockUser("user1")
	void testGetHello() throws Exception {
				
		String uri = STARTING_URI + "/user";

		mvc.perform(get(uri))
		.andDo(print())
		.andExpect (status().isOk())
		;		
	}
	
	@Test
	@WithMockUser("user1")
	void testUpdateUser() throws Exception {
		
		String uri = STARTING_URI + "/user";
		
		Optional<User> usera = Optional.of(new User(1, "N/A", "N/A", Role.ROLE_USER, true));
		User userb = new User(1, "N/A", "N/A", Role.ROLE_USER, true);
		
		
		when(userRepo.findByUsername(Mockito.any(String.class))).thenReturn(usera);
		when(userRepo.save(Mockito.any(User.class))).thenReturn(userb);
		when(userRepo.existsById( Mockito.any(Integer.class))).thenReturn(true);

		mvc.perform( put(uri)
						.contentType( MediaType.APPLICATION_JSON_VALUE )
					 	.content( asJsonString(userb) )  )
			.andDo( print() )
			.andExpect( status().isOk() )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) )
			;	
	}
	
	//CRUD for sales
	@Test
	@WithMockUser("user1")
	void testCustomerInfo() throws Exception {
		
		String uri = STARTING_URI + "/user";
		
		Optional<User> usera = Optional.of(new User(1, "N/A", "N/A", Role.ROLE_USER, true));
		List<Sales> allSales = Arrays.asList( new Sales(null, null, null, 1, 1),
				   							  new Sales(null, null, null, 1, 1) );
		
		when(userRepo.findByUsername(Mockito.any(String.class))).thenReturn(usera);
		when(saleRepo.findByUserId(Mockito.any(Integer.class))).thenReturn(allSales);
	
		mvc.perform( get(uri) ) // perform request...
		.andDo( print() )   // ...then print request sent and response returned
		.andExpect( status().isOk() ) // expect 200 status code
		.andExpect( jsonPath("$.length()").value( allSales.size() ) ) // expected number of elements
		.andExpect( jsonPath("$[0].id").value( allSales.get(0).getId() ) )
		.andExpect( jsonPath("$[0].type").value( allSales.get(0).getProduct() ) )
		.andExpect( jsonPath("$[0].miles").value( allSales.get(0).getTotalAmount() ) )
		.andExpect( jsonPath("$[0].random").value( allSales.get(0).getTotalProduct() ) )
		.andExpect( jsonPath("$[1].id").value( allSales.get(1).getId() ) )
		.andExpect( jsonPath("$[1].type").value( allSales.get(1).getProduct() ) )
		.andExpect( jsonPath("$[1].miles").value( allSales.get(1).getTotalAmount() ) )
		.andExpect( jsonPath("$[1].random").value( allSales.get(1).getTotalProduct() ) )
		;
	
	// verify -> checks how many interactions with code there are
	verify(saleRepo, times(1)).findByUserId(Mockito.any(Integer.class)); // getAllCars() was used once
	verifyNoMoreInteractions(saleRepo); // after checking above, check service is no longer being used	
	}
	
	@Test
	@WithMockUser("user1")
	void testCreateSales(@RequestBody Product product, Principal principal) throws Exception{
		
		String uri = STARTING_URI + "/sales";
		
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
	
	@Test
	void testDeleteSales(@PathVariable int id) throws Exception{
		
		String uri = STARTING_URI + "/sales/{id}";
		
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
	@Test
	void testGetAllProducts() throws Exception {
	
		String uri = STARTING_URI + "/products";
		
	 return productRepo.findAll();
	}
	
	@Test
	void testCreateProduct(@RequestBody Product product) throws Exception {
		
		String uri = STARTING_URI + "/products";
		
		product.setId(null);
		
		Product created = productRepo.save(product);
		
		return ResponseEntity.status(201).body(created);
	}
	
	@Test
	void testUpdateProduct(@RequestBody Product product) throws Exception {
		
		String uri = STARTING_URI + "/products";
		
		if( productRepo.existsById( product.getId() ) ) {
			Product updated = productRepo.save(product);
			
			return ResponseEntity.status(200).body(updated);
		}
		
		throw new ResourceNotFoundException("Product with id = " + product.getId() + " was not found");
	}
	
	@Test
	void TestDeleteProduct() throws Exception {
		
		String uri = STARTING_URI + "/products/{id}";
		int id = 1;
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
	@Test
	@WithMockUser("user1")
	void testCreateAuthenticationToken(@RequestBody AuthenticationRequest request, Principal principal) throws Exception {
		
		String uri = STARTING_URI + "/login";
		
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
	
	@Test 
	void testRemoveAuthenticationToken() throws Exception {	
		
		String uri = STARTING_URI + "/logout";
		
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
	
	public static String asJsonString( final Object obj ) {
		
		try {
			return new ObjectMapper().writeValueAsString(obj);
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}












