package com.abc.productentity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	@GetMapping
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@PostMapping
	public Product createProduct(@Valid @RequestBody Product product) {
		return productRepository.save(product);
	}

	@PutMapping("/{id}")
	public Product updateProduct(@PathVariable @Positive(message = "ID must be a positive number") Long id,
			@Valid @RequestBody Product productDetails) {
		Product product = productRepository.findById(id).orElseThrow();
		product.setName(productDetails.getName());
		product.setPrice(productDetails.getPrice());
		return productRepository.save(product);
	}
}
