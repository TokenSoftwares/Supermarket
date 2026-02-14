package com.pos.supermarketbilling.services.interfaces;

import com.pos.supermarketbilling.models.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<Product> getActive(int page, int size);
    Product getById(Long id);
    Product getByBarcode(String barcode);
    Product create(Product product);
    Product update(Long id, Product product);
    void disable(Long id);
}