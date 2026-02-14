package com.pos.supermarketbilling.controllers;

import com.pos.supermarketbilling.models.Product;
import com.pos.supermarketbilling.services.interfaces.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products?page=0&size=10
    @GetMapping
    public Page<Product> getActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getActive(page, size);
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    // GET /api/products/barcode/{barcode}
    @GetMapping("/barcode/{barcode}")
    public Product getByBarcode(@PathVariable String barcode) {
        return productService.getByBarcode(barcode);
    }

    // POST /api/products
    @PostMapping
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    // PUT /api/products/{id}
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        return productService.update(id, product);
    }

    // DELETE /api/products/{id}  (soft delete)
    @DeleteMapping("/{id}")
    public void disable(@PathVariable Long id) {
        productService.disable(id);
    }
}