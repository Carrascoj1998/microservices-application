package com.jonathan.carrasco.productservice.repository;

import com.jonathan.carrasco.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
