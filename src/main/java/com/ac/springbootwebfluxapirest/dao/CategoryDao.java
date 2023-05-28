package com.ac.springbootwebfluxapirest.dao;


import com.ac.springbootwebfluxapirest.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryDao extends ReactiveMongoRepository<Category, String> {
}
