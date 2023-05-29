package com.ac.springbootwebfluxapirest.dao;


import com.ac.springbootwebfluxapirest.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoryDao extends ReactiveMongoRepository<Category, String> {

    public Mono<Category> findByName(String name);
}
