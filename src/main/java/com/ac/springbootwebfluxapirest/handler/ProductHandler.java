package com.ac.springbootwebfluxapirest.handler;

import com.ac.springbootwebfluxapirest.documents.Product;
import com.ac.springbootwebfluxapirest.services.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
                                .body(BodyInserters.fromValue(p))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
