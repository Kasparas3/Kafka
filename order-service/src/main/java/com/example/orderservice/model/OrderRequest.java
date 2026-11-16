package com.example.orderservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {

	@NotNull
	@Positive
	private Long customerId;

	@NotBlank
	private String product;

	@NotNull
	@Positive
	private Integer quantity;

}
