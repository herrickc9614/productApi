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


@Entity
public class Sales {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer saleId;
	
	@ManyToOne
    @JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
    @JoinColumn(name = "product_id")
	private Product product;
	
	@Column(nullable = false)
	private Integer totalProduct;

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

}
