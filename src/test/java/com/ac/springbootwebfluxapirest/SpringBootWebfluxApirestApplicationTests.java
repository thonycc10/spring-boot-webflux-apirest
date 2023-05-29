package com.ac.springbootwebfluxapirest;

import com.ac.springbootwebfluxapirest.documents.Category;
import com.ac.springbootwebfluxapirest.documents.Product;
import com.ac.springbootwebfluxapirest.services.CategoryService;
import com.ac.springbootwebfluxapirest.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import javax.xml.catalog.CatalogException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @Value("${config.base.endpoint.controller.product}")
    private String url;

    @Test
    public void creatTest2() { // mejor usar este tipo
        Category category = categoryService.findByName("Electronico").block();

        Product product = new Product("Mesa Comedor", 100.00, category);

        webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {})
                .consumeWith(response -> {
                    Object o = response.getResponseBody().get("product");
                    Product p = new ObjectMapper().convertValue(o, Product.class);
                    Assertions.assertNotNull(p.getId());
                    Assertions.assertEquals("Mesa Comedor", p.getName());
                    Assertions.assertEquals("Electronico", p.getCategory().getName());
                });
    }

   /* @Test
    public void viewTest() {
        Product product = productService.findByName("Laptop1").block();

        webTestClient.get()
                .uri(url + "/{id}", Collections.singletonMap("id", product.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                   Product p = response.getResponseBody();
                   Assertions.assertNotNull(p.getId());
                   Assertions.assertTrue(p.getId().length()>0);
                   Assertions.assertEquals(p.getName(), "Laptop1");
                });
    }

    @Test
    public void creatTest() {
        Category category = categoryService.findByName("Electronico").block();

        Product product = new Product("Mesa Comedor", 100.00, category);

        webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Mesa Comedor")
                .jsonPath("$.category.name").isEqualTo("Electronico");
    }

    @Test
    public void creatTest2() { // mejor usar este tipo
        Category category = categoryService.findByName("Electronico").block();

        Product product = new Product("Mesa Comedor", 100.00, category);

        webTestClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Product.class)
                .consumeWith(response -> {
                   Product p = response.getResponseBody();
                   Assertions.assertNotNull(p.getId());
                   Assertions.assertEquals("Mesa Comedor", p.getName());
                   Assertions.assertEquals("Electronico", p.getCategory().getName());
                });
    }

    @Test
    public void EditTest() {
        Product product = productService.findByName("Laptop2").block();
        Category category = categoryService.findByName("Electronico").block();

        Product productEdit = new Product("Asus", 800.00, category);

        webTestClient.put().uri(url + "/{id}", Collections.singletonMap("id", product.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(productEdit), Product.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Asus")
                .jsonPath("$.category.name").isEqualTo("Electronico");

    }

    *//*@Test
    public void viewTest() {
        Product product = productService.findByName("Laptop1").block();

        webTestClient.get()
                .uri("url/{id}", Collections.singletonMap("id", product.getId()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("Laptop 1");
    }*//*

    @Test
    public void listTest() {
        webTestClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .consumeWith(r -> {
                    List<Product> products = r.getResponseBody();

                    products.forEach(p -> {
                        System.out.println(p.getName());
                    });

                    Assertions.assertEquals(6, products.size());
                });
    }

    @Test
    public void DeleteTest() {
        Product product = productService.findByName("Laptop2").block();

        webTestClient.delete()
                .uri(url + "/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

        webTestClient.get()
                .uri(url + "/{id}", Collections.singletonMap("id", product.getId()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

    *//*@Test
    public void listTest() {
        webTestClient.get()
                .uri("url")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Product.class)
                .hasSize(5); // limite
    }*/

}
