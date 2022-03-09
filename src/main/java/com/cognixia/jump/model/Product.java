package com.cognixia.jump.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id")
	private Integer productID;
	
	@Column(nullable = false)
	private String productName;
	
	@Column(nullable = false)
	private Integer productCost;
	
	@Column(nullable = false)
	private Integer totalProduct;
	
	public Product() {
		this(null, "N/A", 1, 1);
	}
	
	public Product(Integer productID, String productName, int productCost, int totalproduct) {
		super();
		this.productID = productID;
		this.productName = productName;
		this.productCost = productCost;
		this.totalProduct = totalproduct;
	}

	public Integer getId() {
		return productID;
	}

	public void setId(Integer id) {
		this.productID = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductCost() {
		return productCost;
	}

	public void setProductCost(Integer productCost) {
		this.productCost = productCost;
	}

	public Integer getTotalProduct() {
		return totalProduct;
	}

	public void setTotalProduct(Integer totalProduct) {
		this.totalProduct = totalProduct;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
