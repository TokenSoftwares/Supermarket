package com.pos.supermarketbilling.daos;

import com.pos.supermarketbilling.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillDao extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);
}