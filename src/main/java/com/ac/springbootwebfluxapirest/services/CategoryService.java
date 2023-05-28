package com.ac.springbootwebfluxapirest.services;

import com.ac.springbootwebfluxapirest.documents.Category;
import com.ac.springbootwebfluxapirest.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    public Flux<Category> findAll();
    public Mono<Category> findBiId(String id);
    public Mono<Category> save(Category category);
    public Mono<Void> delete(Category category);
}
