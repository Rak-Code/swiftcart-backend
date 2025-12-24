package com.ecommerce.project.controller;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(@PathVariable String orderId) {
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String invoiceId) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{invoiceId}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable String invoiceId) {
        byte[] pdfData = invoiceService.downloadInvoicePdf(invoiceId);
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", invoice.getInvoiceNumber() + ".pdf");
        headers.setContentLength(pdfData.length);

        return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
    }

    @GetMapping("/download-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadAllInvoices() {
        ByteArrayOutputStream zipStream = invoiceService.downloadAllInvoicesZip();
        byte[] zipData = zipStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "all-invoices.zip");
        headers.setContentLength(zipData.length);

        return new ResponseEntity<>(zipData, headers, HttpStatus.OK);
    }
}
