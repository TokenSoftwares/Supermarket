package com.pos.supermarketbilling.services.implementations;

import com.pos.supermarketbilling.daos.ProductDao;
import com.pos.supermarketbilling.exceptions.BadRequestException;
import com.pos.supermarketbilling.exceptions.ResourceNotFoundException;
import com.pos.supermarketbilling.models.Product;
import com.pos.supermarketbilling.services.interfaces.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImp implements ProductService {

    private final ProductDao productDao;

    public ProductServiceImp(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Page<Product> getActive(int page, int size) {
        return productDao.findByActiveTrue(PageRequest.of(page, size));
    }

    @Override
    public Product getById(Long id) {
        return productDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product getByBarcode(String barcode) {
        return productDao.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
    }

    @Override
    public Product create(Product product) {
        if (productDao.existsByBarcode(product.getBarcode())) {
            throw new BadRequestException("Barcode already exists");
        }
        if (product.getActive() == null) product.setActive(true);
        return productDao.save(product);
    }

    @Override
    public Product update(Long id, Product product) {
        Product existing = getById(id);

        if (product.getBarcode() != null && !product.getBarcode().equals(existing.getBarcode())) {
            if (productDao.existsByBarcode(product.getBarcode())) {
                throw new BadRequestException("Barcode already exists");
            }
            existing.setBarcode(product.getBarcode());
        }

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        if (product.getActive() != null) existing.setActive(product.getActive());

        return productDao.save(existing);
    }

    @Override
    public void disable(Long id) {
        Product p = getById(id);
        p.setActive(false);
        productDao.save(p);
    }
}