package com.ac.springbootwebfluxapirest.controllers;

import com.ac.springbootwebfluxapirest.documents.Product;
import com.ac.springbootwebfluxapirest.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping({"/api/product", "/"})
public class ProductController {

    private final ProductService service;

    @Value("${config.upload.path}")
    private String pathImg;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> list() {
        return Mono.just(
                ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> view(@PathVariable String id) {
        return service.findBiId(id)
                .map(p -> ResponseEntity
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String , Object>>> create(@Valid @RequestBody Mono<Product> monoProduct) {

        Map<String, Object> response = new HashMap<String, Object>();

        return monoProduct.flatMap(product -> {
            if (product.getCreateAt() == null ) {
                product.setCreateAt(new Date());
            }

            return service.save(product)
                    .map(p -> {
                        response.put("product", p);
                        response.put("menssage", "Producto success");
                        response.put("timestamp", new Date());
                        return ResponseEntity
                                .created(URI.create("/api/prodcut/".concat(p.getId())))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(response);
                    });
        }).onErrorResume(t -> {
           return Mono.just(t)
                   .cast(WebExchangeBindException.class)
                   .flatMap(e -> Mono.just(e.getFieldErrors()))
                   .flatMapMany(Flux::fromIterable)
                   .map(fieldError -> "The field " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                   .collectList()// convert flux to list mono string
                   .flatMap(list -> {
                       response.put("erros", list);
                       response.put("timestamp", new Date());
                       response.put("status", HttpStatus.BAD_REQUEST.value());
                       return Mono.just(ResponseEntity.badRequest().body(response));
                   });
        });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> update(@RequestBody Product product, @PathVariable String id) {
        return service.findBiId(id)
                .flatMap(p -> {
                    p.setName(product.getName());
                    p.setPrice(product.getPrice());
                    p.setCategory(product.getCategory());
                    return service.save(p);
                })
                .map(p -> ResponseEntity
                        .created(URI.create("/api/product/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.findBiId(id)
                .flatMap(p -> {
                    return service.delete(p)
                            .then(Mono.just(
                                    new ResponseEntity<Void>(HttpStatus.NO_CONTENT)
                            ));
                })
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Product>> uploadImg(@PathVariable String id, @RequestPart FilePart file) {
        return service.findBiId(id)
                .flatMap(p -> {
                    p.setPicture(
                            UUID.randomUUID().toString() + "-" + file.filename()
                                    .replace(" ", "")
                                    .replace(":", "")
                                    .replace("\\", ""));
                    return file.transferTo(new File(pathImg + p.getPicture()))
                            .then(
                                    service.save(p)
                            );
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/updateimgv2")
    public Mono<ResponseEntity<Product>> uploadImgAndCreateProduct(Product product, @RequestPart FilePart file) {

        if (product.getCreateAt() == null) {
            product.setCreateAt(new Date());
        }

        product.setPicture(UUID.randomUUID().toString() + "-" + file.filename()
                .replace(" ", "")
                .replace(":", "")
                .replace("\\", "")
        );

        return file.transferTo(new File(pathImg + product.getPicture()))
                .then(service.save(product))
                .map(p -> ResponseEntity
                        .created(URI.create("/api/product/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                );
    }

}
