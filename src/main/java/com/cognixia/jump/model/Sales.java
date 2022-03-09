package com.cognixia.jump.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import io.swagger.v3.oas.annotations.media.Schema;


@Entity
public class Sales {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "ID for Sales",
	example = "1", 
	required = true)
	private Integer saleId;
	
	@ManyToOne
	@Schema(description = "Connects the User table by userID", 
	required = true)
    @JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@Schema(description = "Connects the Products table by productID",
	required = true)
    @JoinColumn(name = "product_id")
	private Product product;
	
	@Schema(description = "Total Amount Purchased",
			example = "1", 
			required = true)
	@Column(nullable = false)
	private Integer totalProduct;

	@Schema(description = "Total Dollar Amount",
			example = "1", 
			required = true)
	@Column(nullable = false)
	private Integer totalAmount;

	
	public Sales() {
		this(null, null, null, 1, 1);
	}
	
	public Sales(Integer id, User user, Product product, int totalproduct, int totalAmount) {
		super();
		this.saleId = id;
		this.user = user;
		this.product = product;
		this.totalProduct = totalproduct;
		this.totalAmount = totalAmount;
	}

	public Integer getId() {
		return saleId;
	}

	public void setId(Integer id) {
		this.saleId = id;
	}

	public Integer getTotalProduct() {
		return totalProduct;
	}

	public void setTotalProduct(Integer totalProduct) {
		this.totalProduct = totalProduct;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getUser() {
		return user.getUsername();
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public String getProduct() {
		return product.getProductName();
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	

}
