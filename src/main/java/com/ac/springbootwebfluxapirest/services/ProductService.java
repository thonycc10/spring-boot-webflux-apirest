package com.ac.springbootwebfluxapirest.services;

import com.ac.springbootwebfluxapirest.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    public Flux<Product> findAll();
    public Flux<Product> findAllWithNameUpperCase();
    public Flux<Product> findAllWithNameUpperCaseRepeat(Integer top);
    public Mono<Product> findBiId(String id);
    public Mono<Product> save(Product product);
    public Mono<Void> delete(Product product);
}
