package com.ac.springbootwebfluxapirest.documents;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotNull
    private Double price;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;
    @Valid
    private Category category;
    private String picture;

//    Se crea un constructor vacio para que Spring data cree los objetos vacios al iniciar

    public Product() {
    }

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }
    public Product(String name, Double price, Category category) {
        this(name, price);
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
