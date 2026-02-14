package com.pos.supermarketbilling.services.implementations;

import com.pos.supermarketbilling.daos.BillDao;
import com.pos.supermarketbilling.daos.CustomerDao;
import com.pos.supermarketbilling.daos.ProductDao;
import com.pos.supermarketbilling.exceptions.BadRequestException;
import com.pos.supermarketbilling.exceptions.InsufficientStockException;
import com.pos.supermarketbilling.exceptions.ResourceNotFoundException;
import com.pos.supermarketbilling.models.Bill;
import com.pos.supermarketbilling.models.Billitem;
import com.pos.supermarketbilling.models.Customer;
import com.pos.supermarketbilling.models.Product;
import com.pos.supermarketbilling.models.dtos.CreateBillRequest;
import com.pos.supermarketbilling.models.dtos.CreateBillitemRequest;
import com.pos.supermarketbilling.services.interfaces.BillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@Transactional
public class BillServiceImp implements BillService {

    private final BillDao billDao;
    private final ProductDao productDao;
    private final CustomerDao customerDao;

    public BillServiceImp(BillDao billDao, ProductDao productDao, CustomerDao customerDao) {
        this.billDao = billDao;
        this.productDao = productDao;
        this.customerDao = customerDao;
    }

    @Override
    public Bill create(CreateBillRequest request) {

        Bill bill = new Bill();
        bill.setBillNumber(generateBillNumber());
        bill.setCreatedAt(LocalDateTime.now());
        bill.setPaymentMethod(request.getPaymentMethod());

        if (request.getCustomerId() != null) {
            Customer customer = customerDao.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with id: " + request.getCustomerId()
                    ));
            if (Boolean.FALSE.equals(customer.getActive())) {
                throw new BadRequestException("Customer is inactive: " + customer.getId());
            }
            bill.setCustomer(customer);
        }

        double subTotal = 0.0;
        double discountTotal = 0.0;
        double taxTotal = 0.0; // keep 0 for now
        var items = new ArrayList<Billitem>();

        for (CreateBillitemRequest itemReq : request.getItems()) {

            Product product = productDao.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemReq.getProductId()
                    ));

            if (Boolean.FALSE.equals(product.getActive())) {
                throw new BadRequestException("Product is inactive: " + product.getId());
            }

            int qty = itemReq.getQuantity();
            if (product.getStock() < qty) {
                throw new InsufficientStockException(
                        "Insufficient stock for product " + product.getName()
                                + " (available: " + product.getStock()
                                + ", requested: " + qty + ")"
                );
            }

            double unitPrice = product.getPrice();
            double lineBase = unitPrice * qty;

            double discount = itemReq.getDiscount() == null ? 0.0 : itemReq.getDiscount();
            if (discount < 0) discount = 0.0;
            if (discount > lineBase) discount = lineBase;

            double lineTotal = lineBase - discount;

            Billitem billItem = new Billitem();
            billItem.setBill(bill);
            billItem.setProduct(product);
            billItem.setQuantity(qty);
            billItem.setUnitPrice(unitPrice);
            billItem.setDiscount(discount);
            billItem.setLineTotal(lineTotal);

            items.add(billItem);

            subTotal += lineBase;
            discountTotal += discount;

            // deduct stock (no need to save product explicitly; JPA will flush on commit)
            product.setStock(product.getStock() - qty);
        }

        double grandTotal = subTotal - discountTotal + taxTotal;

        bill.setItems(items);
        bill.setSubTotal(subTotal);
        bill.setDiscountTotal(discountTotal);
        bill.setTaxTotal(taxTotal);
        bill.setGrandTotal(grandTotal);

        return billDao.save(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public Bill getById(Long id) {
        return billDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
    }

    private String generateBillNumber() {
        return "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}