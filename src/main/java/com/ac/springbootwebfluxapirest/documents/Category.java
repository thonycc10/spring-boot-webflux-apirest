package com.ac.springbootwebfluxapirest.documents;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

public class Category {
    @Id
    @NotEmpty
    private String id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
