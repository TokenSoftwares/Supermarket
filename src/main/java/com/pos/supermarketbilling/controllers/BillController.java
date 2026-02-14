package com.pos.supermarketbilling.controllers;

import com.pos.supermarketbilling.models.Bill;
import com.pos.supermarketbilling.models.dtos.CreateBillRequest;
import com.pos.supermarketbilling.services.interfaces.BillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    // POST /api/bills
    @PostMapping
    public Bill create(@Valid @RequestBody CreateBillRequest request) {
        return billService.create(request);
    }

    // GET /api/bills/{id}
    @GetMapping("/{id}")
    public Bill getById(@PathVariable Long id) {
        return billService.getById(id);
    }
}