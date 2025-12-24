package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    
    Optional<Invoice> findByOrderId(String orderId);
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByUserId(String userId);
    
    List<Invoice> findAllByOrderByInvoiceDateDesc();
}
