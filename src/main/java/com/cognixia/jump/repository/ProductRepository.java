package com.cognixia.jump.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognixia.jump.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	// used to find a user by their username
	// Optional is used in case username given is not in table
	public List<Product> findAll();
	
}
