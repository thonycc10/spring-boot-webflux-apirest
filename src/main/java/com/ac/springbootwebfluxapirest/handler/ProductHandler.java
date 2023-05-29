package com.ac.springbootwebfluxapirest.handler;

import com.ac.springbootwebfluxapirest.documents.Category;
import com.ac.springbootwebfluxapirest.documents.Product;
import com.ac.springbootwebfluxapirest.services.ProductService;
import com.mongodb.internal.connection.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ProductHandler {

    private final ProductService service;
    private final Validator validator;

    @Value("${config.upload.path}")
    private String pathImg;

    public ProductHandler(ProductService service, Validator validator) {
        this.service = service;
        this.validator = validator;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> productById(ServerRequest request) {
        String id = request.pathVariable("id");
        return service.findBiId(id).flatMap(p ->
                        ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(p))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.flatMap(p -> {

            Errors errors = new BeanPropertyBindingResult(p, Product.class.getName());
            validator.validate(p, errors);

            if (errors.hasErrors()) {
                return Flux.fromIterable(errors.getFieldErrors())
                        .map(fieldError -> "The field " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                        .collectList()
                        .flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
            } else {
                if (p.getCreateAt() == null) {
                    p.setCreateAt(new Date());
                }

                return service.save(p).flatMap(pbd -> ServerResponse
                        .created(URI.create("api/v2/products/".concat(pbd.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(pbd)));
            }
        });
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<Product> productMono = request.bodyToMono(Product.class);
        String id = request.pathVariable("id");

        Mono<Product> productMonoDB = service.findBiId(id);

        return productMonoDB.zipWith(productMono, (pdb, p) -> {
            pdb.setName(p.getName());
            pdb.setPrice(p.getPrice());
            pdb.setCategory(p.getCategory());
            return pdb;
        }).flatMap(p -> ServerResponse
                .created(URI.create("/api/v2/products/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.save(p), Product.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Product> productMonoDB = service.findBiId(id);

        return productMonoDB.flatMap(p ->
                service.delete(p).then(ServerResponse.noContent().build())
        ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> uploadImg(ServerRequest request) {
        String id = request.pathVariable("id");

        return request.multipartData()
                .map(files -> files.toSingleValueMap().get("file")) // variable KEY captura el valor
                .cast(FilePart.class)
                .flatMap(file ->
                        service.findBiId(id)
                                .flatMap(p -> {
                                    p.setPicture(UUID.randomUUID().toString() + "-" + file.filename()
                                            .replace(" ", "")
                                            .replace(":", "")
                                            .replace("\\", ""));
                                    return file.transferTo(new File(pathImg + p.getPicture()))
                                            .then(service.save(p));
                                })).flatMap(p ->
                        ServerResponse.created(URI.create("/api/v2/products/".concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(p))
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createProductAndUpdateImg(ServerRequest request) {
        Mono<Product> productMono = request.multipartData().map(multipart -> {
            FormFieldPart name = (FormFieldPart) multipart.toSingleValueMap().get("name");
            FormFieldPart price = (FormFieldPart) multipart.toSingleValueMap().get("price");
            FormFieldPart categoryId = (FormFieldPart) multipart.toSingleValueMap().get("category.id");
            FormFieldPart categoryName = (FormFieldPart) multipart.toSingleValueMap().get("category.name");
            Category category = new Category(categoryName.value());
            category.setId(categoryId.value());
            return new Product(name.value(), Double.parseDouble(price.value()), category);
        });

        return request.multipartData()
                .map(files -> files.toSingleValueMap().get("file")) // variable KEY captura el valor
                .cast(FilePart.class)
                .flatMap(file ->
                        productMono.flatMap(p -> {
                                    p.setPicture(UUID.randomUUID().toString() + "-" + file.filename()
                                            .replace(" ", "")
                                            .replace(":", "")
                                            .replace("\\", ""));

                                    p.setCreateAt(new Date());

                                    return file.transferTo(new File(pathImg + p.getPicture()))
                                            .then(service.save(p));
                                })).flatMap(p ->
                        ServerResponse.created(URI.create("/api/v2/products/".concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fromValue(p))
                );
    }

}
