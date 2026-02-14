package com.pos.supermarketbilling.daos;

import com.pos.supermarketbilling.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerDao extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Page<Customer> findByActiveTrueAndFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}