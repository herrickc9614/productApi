package com.cognixia.jump.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
public class User {

	public static enum Role {
		ROLE_USER, ROLE_ADMIN // roles need to be capital and start with ROLE_
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "ID for User",
	example = "1", 
	required = true)
	@Column(name="user_id")
	private Integer userID;
	
	@Schema(description = "Accounts username for user",
			example = "user1", 
			required = true)
	@Column(unique = true, nullable = false)
	@NotNull(message = "userName cannot be null")
	private String username;
	
	@Schema(description = "Accounts password for user",
			example = "password", 
			required = true)
	@Column(nullable = false)
	@NotNull(message = "Password cannot be null")
	private String password;
	
	@Schema(description = "Role of current user",
			example = "ROLE_USER", 
			required = true)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@NotNull(message = "Role cannot be null")
	private Role role;
	
	@Schema(description = "checks if user is active acount",
			example = "true", 
			required = true)
	@Column(columnDefinition = "boolean default true")
	private boolean enabled;

	
	public User() {
		this(null, "N/A", "N/A", Role.ROLE_USER, true);
	}
	
	public User(Integer id, String username, String password, Role role, boolean enabled) {
		super();
		this.userID = id;
		this.username = username;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
	}

	public Integer getId() {
		return userID;
	}

	public void setId(Integer id) {
		this.userID = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
