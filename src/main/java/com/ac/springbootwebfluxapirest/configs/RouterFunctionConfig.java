package com.ac.springbootwebfluxapirest.configs;

import com.ac.springbootwebfluxapirest.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterFunctionConfig {

    @Bean
    public RouterFunction<ServerResponse> routes(ProductHandler productHandler) {
        return route(GET("/api/v2/products").or(GET("/api/v3/products")), productHandler::list)
                .andRoute(GET("/api/v2/products/{id}"), productHandler::productById);
//        se usa esto para solicitar las peticiones con header
//                .andRoute(GET("/api/v2/products/{id}").and(contentType(MediaType.APPLICATION_JSON)), productHandler::productById);
    }

}
