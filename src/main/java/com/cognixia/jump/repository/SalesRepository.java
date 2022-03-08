package com.cognixia.jump.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cognixia.jump.model.Sales;
import com.cognixia.jump.model.User;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Integer> {

	// used to find a user by their username
	// Optional is used in case username given is not in table
	@Query("select u from Sales u where user_id = ?1")
	public List<Sales> findByUserId(Integer user_id);
	
}
