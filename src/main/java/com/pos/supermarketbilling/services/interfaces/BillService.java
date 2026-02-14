package com.pos.supermarketbilling.services.interfaces;

import com.pos.supermarketbilling.models.Bill;
import com.pos.supermarketbilling.models.dtos.CreateBillRequest;

public interface BillService {
    Bill create(CreateBillRequest request);
    Bill getById(Long id);
}