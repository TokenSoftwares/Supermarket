package com.pos.supermarketbilling.controllers;

import com.pos.supermarketbilling.models.Customer;
import com.pos.supermarketbilling.services.interfaces.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /api/customers?q=ali&page=0&size=10
    @GetMapping
    public Page<Customer> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return customerService.searchActive(q, page, size);
    }

    // GET /api/customers/{id}
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return customerService.getById(id);
    }

    // POST /api/customers
    @PostMapping
    public Customer create(@Valid @RequestBody Customer customer) {
        return customerService.create(customer);
    }

    // PUT /api/customers/{id}
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        return customerService.update(id, customer);
    }

    // DELETE /api/customers/{id}  (soft delete)
    @DeleteMapping("/{id}")
    public void disable(@PathVariable Long id) {
        customerService.disable(id);
    }
}