package com.ac.springbootwebfluxapirest.dao;

import com.ac.springbootwebfluxapirest.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {

}
