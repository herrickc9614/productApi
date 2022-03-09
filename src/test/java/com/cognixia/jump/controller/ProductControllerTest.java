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
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

	
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
	private ProductController controller;
		
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
	void testCreateSales() throws Exception{
		
		String uri = STARTING_URI + "/sales";
		
		String temp = "user1";
		int id = 1;
		
		
		Product product = new Product(1, "N/A", 1, 1);
		Sales sales = new Sales(1, null, product, 1, 1);
		Optional<User> usera = Optional.of(new User(1, "N/A", "N/A", Role.ROLE_USER, true));
		
		when(userRepo.findByUsername(temp)).thenReturn(usera);
		when(productRepo.existsById(id)).thenReturn(true);
		when(saleRepo.save(Mockito.any(Sales.class))).thenReturn(sales);

		// need to convert and send car object in json format
		mvc.perform( post(uri)
						.contentType( MediaType.APPLICATION_JSON_VALUE )
					 	.content( asJsonString(sales) )  )
			.andDo( print() )
			.andExpect( status().isCreated() )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) )
			;
	}
	
	@Test
	void testDeleteSales() throws Exception{
		
		int id = 1;
		String uri = STARTING_URI + "/sales/{id}";
		Optional<Sales> salesa = Optional.of(new Sales(null, null, null, 1, 1));
		
		when(saleRepo.existsById(id)).thenReturn(true);
		when(saleRepo.findById(id)).thenReturn(salesa);
		
		
		mvc.perform( delete(uri, id))
		.andDo( print() )
		.andExpect( status().isOk() )
		.andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) )
		;
	
	verify(saleRepo, times(1)).deleteById(id);
	verifyNoMoreInteractions(saleRepo);
	}
	
	
	//CRUD for products
	@Test
	void testGetAllProducts() throws Exception {

		String uri = STARTING_URI + "/products";
		
		List<Product> allProducts = Arrays.asList( new Product(null, "N/A", 1, 1),
													new Product(null, "N/A", 1, 1) );
		
		when(productRepo.findAll()).thenReturn(allProducts);			
		
		mvc.perform( get(uri) ) // perform request...
			.andDo( print() )   // ...then print request sent and response returned
			.andExpect( status().isOk() ) // expect 200 status code
			.andExpect( jsonPath("$.length()").value( allProducts.size() ) ) // expected number of elements
			.andExpect( jsonPath("$[0].id").value( allProducts.get(0).getId() ) )
			.andExpect( jsonPath("$[0].name").value( allProducts.get(0).getProductName() ) )
			.andExpect( jsonPath("$[0].productCost").value( allProducts.get(0).getProductCost() ) )
			.andExpect( jsonPath("$[0].totalProduct").value( allProducts.get(0).getTotalProduct() ) )
			.andExpect( jsonPath("$[1].id").value( allProducts.get(1).getId() ) )
			.andExpect( jsonPath("$[1].name").value( allProducts.get(1).getProductName() ) )
			.andExpect( jsonPath("$[1].productCost").value( allProducts.get(1).getProductCost() ) )
			.andExpect( jsonPath("$[1].totalProduct").value( allProducts.get(1).getTotalProduct() ) )
			;
		
		// verify -> checks how many interactions with code there are
		verify(productRepo, times(1)).findAll(); // getAllCars() was used once
		verifyNoMoreInteractions(productRepo); // after checking above, check service is no longer being used
		
	}
	
	@Test
	void testCreateProduct() throws Exception {
		
		String uri = STARTING_URI + "/products";
		
		Product product = new Product(1, "N/A", 1, 1);
		
		when(productRepo.save(Mockito.any(Product.class))).thenReturn(product);
		
		// need to convert and send car object in json format
		mvc.perform( post(uri)
						.contentType( MediaType.APPLICATION_JSON_VALUE )
					 	.content( asJsonString(product) )  )
			.andDo( print() )
			.andExpect( status().isCreated() )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) )
			;
	}
	
	@Test
	void testUpdateProduct() throws Exception {
		
		String uri = STARTING_URI + "/products";
		
		Product product = new Product(null, "N/A", 1, 1);
		when(productRepo.existsById(Mockito.any(Integer.class))).thenReturn(true);
		when(productRepo.save(Mockito.any(Product.class))).thenReturn(product);		

		mvc.perform( put(uri)
				.contentType( MediaType.APPLICATION_JSON_VALUE )
			 	.content( asJsonString(product) )  )
		.andDo( print() )
		.andExpect( status().isOk() )
		.andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) )
		;	
	}
	
	@Test
	void TestDeleteProduct() throws Exception {
		
		String uri = STARTING_URI + "/products/{id}";
		int id = 1;
		
		Optional<Product> product = Optional.of(new Product(null, "N/A", 1, 1));
		
		when(productRepo.existsById(Mockito.any(Integer.class))).thenReturn(true);
		when(productRepo.findById(Mockito.any(Integer.class))).thenReturn(product);
		
		
		mvc.perform( delete(uri, id))
		.andDo( print() )
		.andExpect( status().isOk() )
		.andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) )
		;
	
		verify(productRepo, times(1)).deleteById(id);
		verifyNoMoreInteractions(productRepo);
	}
	

	
	public static String asJsonString( final Object obj ) {
		
		try {
			return new ObjectMapper().writeValueAsString(obj);
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
}












