package com.abc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
	@Id
	@GeneratedValue
	private Integer tokenId;
	private String token;
	private String tokenType;
	private boolean expired;
	private boolean revoked;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

}
