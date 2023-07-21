package com.jonathan.carrasco.productservice.controller;

import com.jonathan.carrasco.productservice.dto.ProductRequest;
import com.jonathan.carrasco.productservice.dto.ProductResponse;
import com.jonathan.carrasco.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;



    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProduct(){
        return productService.getAllProducts();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct (@RequestBody ProductRequest productRequest){
        productService.createProduct(productRequest);
    }





}
