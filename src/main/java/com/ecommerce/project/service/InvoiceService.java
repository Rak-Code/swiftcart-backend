package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.User;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface InvoiceService {
    
    Invoice generateInvoice(Order order, User user);
    
    ByteArrayOutputStream generateInvoicePdf(String invoiceId);
    
    Invoice getInvoiceByOrderId(String orderId);
    
    Invoice getInvoiceById(String invoiceId);
    
    List<Invoice> getAllInvoices();
    
    byte[] downloadInvoicePdf(String invoiceId);
    
    ByteArrayOutputStream downloadAllInvoicesZip();
}
