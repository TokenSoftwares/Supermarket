package com.pos.supermarketbilling.services.implementations;

import com.pos.supermarketbilling.daos.CustomerDao;
import com.pos.supermarketbilling.exceptions.BadRequestException;
import com.pos.supermarketbilling.exceptions.ResourceNotFoundException;
import com.pos.supermarketbilling.models.Customer;
import com.pos.supermarketbilling.services.interfaces.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CustomerServiceImp implements CustomerService {

    private final CustomerDao customerDao;

    public CustomerServiceImp(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public Page<Customer> searchActive(String q, int page, int size) {
        String query = (q == null) ? "" : q.trim();
        return customerDao.findByActiveTrueAndFullNameContainingIgnoreCase(query, PageRequest.of(page, size));
    }

    @Override
    public Customer getById(Long id) {
        return customerDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public Customer create(Customer customer) {
        customer.setCreatedAt(LocalDateTime.now());
        if (customer.getActive() == null) customer.setActive(true);

        // optional uniqueness checks (phone/email can be null)
        if (customer.getPhone() != null && !customer.getPhone().isBlank()
                && customerDao.existsByPhone(customer.getPhone())) {
            throw new BadRequestException("Phone already exists");
        }
        if (customer.getEmail() != null && !customer.getEmail().isBlank()
                && customerDao.existsByEmail(customer.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        return customerDao.save(customer);
    }

    @Override
    public Customer update(Long id, Customer customer) {
        Customer existing = getById(id);

        String newPhone = customer.getPhone();
        if (newPhone != null && !newPhone.isBlank()
                && !newPhone.equals(existing.getPhone())
                && customerDao.existsByPhone(newPhone)) {
            throw new BadRequestException("Phone already exists");
        }

        String newEmail = customer.getEmail();
        if (newEmail != null && !newEmail.isBlank()
                && !newEmail.equals(existing.getEmail())
                && customerDao.existsByEmail(newEmail)) {
            throw new BadRequestException("Email already exists");
        }

        existing.setFullName(customer.getFullName());
        existing.setPhone(customer.getPhone());
        existing.setEmail(customer.getEmail());
        if (customer.getActive() != null) existing.setActive(customer.getActive());

        return customerDao.save(existing);
    }

    @Override
    public void disable(Long id) {
        Customer c = getById(id);
        c.setActive(false);
        customerDao.save(c);
    }
}