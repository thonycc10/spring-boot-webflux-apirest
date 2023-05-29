package com.ac.springbootwebfluxapirest.dao;

import com.ac.springbootwebfluxapirest.documents.Product;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductDao extends ReactiveMongoRepository<Product, String> {

    public Mono<Product> findByName(String name);

    @Query("{'name':  ?0 }")
    public Mono<Product> getByName(String name);

}
