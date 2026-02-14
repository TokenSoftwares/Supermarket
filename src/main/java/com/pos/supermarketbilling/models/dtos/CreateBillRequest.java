package com.pos.supermarketbilling.models.dtos;

import com.pos.supermarketbilling.models.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateBillRequest {

    private Long customerId;

    @NotNull
    private PaymentMethod paymentMethod;

    @Valid
    @NotNull
    private List<CreateBillitemRequest> items;
}