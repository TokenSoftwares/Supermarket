package com.pos.supermarketbilling.daos;

import com.pos.supermarketbilling.models.Billitem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillitemDao extends JpaRepository<Billitem, Long> {
    List<Billitem> findByBill_Id(Long billId);
}