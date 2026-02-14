package com.pos.supermarketbilling.services.interfaces;

import com.pos.supermarketbilling.models.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {
    Page<Customer> searchActive(String q, int page, int size);
    Customer getById(Long id);
    Customer create(Customer customer);
    Customer update(Long id, Customer customer);
    void disable(Long id);
}