package com.ac.springbootwebfluxapirest.handler;

import com.ac.springbootwebfluxapirest.documents.Product;
import com.ac.springbootwebfluxapirest.services.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ProductHandler {

    private final ProductService service;

    public ProductHandler(ProductService service) {
        this.service = service;
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
            if (p.getCreateAt() == null) {
                p.setCreateAt(new Date());
            }

            return service.save(p);
        }).flatMap(p -> ServerResponse
                .created(URI.create("api/v2/products/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(p))
        );
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


}
