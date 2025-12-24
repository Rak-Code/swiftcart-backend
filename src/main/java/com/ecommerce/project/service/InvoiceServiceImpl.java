package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final StorageService storageService;

    @Override
    public Invoice generateInvoice(Order order, User user) {
        try {
            // Check if invoice already exists
            Optional<Invoice> existingInvoice = invoiceRepository.findByOrderId(order.getId());
            if (existingInvoice.isPresent()) {
                log.info("Invoice already exists for order: {}", order.getId());
                return existingInvoice.get();
            }

            // Generate invoice number
            String invoiceNumber = generateInvoiceNumber();

            // Calculate amounts (assuming 18% GST)
            double totalAmount = order.getTotalAmount();
            double subtotal = totalAmount / 1.18;
            double taxAmount = totalAmount - subtotal;

            // Create invoice entity
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber(invoiceNumber);
            invoice.setOrderId(order.getId());
            invoice.setUserId(order.getUserId());
            invoice.setCustomerName(user.getFullName());
            invoice.setCustomerEmail(user.getEmail());
            invoice.setTotalAmount(totalAmount);
            invoice.setSubtotal(subtotal);
            invoice.setTaxAmount(taxAmount);
            invoice.setInvoiceDate(order.getOrderDate());
            invoice.setGeneratedAt(LocalDateTime.now());

            // Generate PDF
            ByteArrayOutputStream pdfStream = generateInvoicePdfInternal(order, user, invoice);
            
            // Upload to R2/S3
            String fileName = "invoices/" + invoiceNumber + ".pdf";
            String pdfUrl = storageService.uploadFile(pdfStream.toByteArray(), fileName, "application/pdf");
            invoice.setPdfPath(pdfUrl);

            // Save invoice
            Invoice savedInvoice = invoiceRepository.save(invoice);
            log.info("Invoice generated successfully: {}", invoiceNumber);

            return savedInvoice;

        } catch (Exception e) {
            log.error("Failed to generate invoice for order: {}", order.getId(), e);
            throw new RuntimeException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public ByteArrayOutputStream generateInvoicePdf(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        // Fetch order and user details (you'll need to inject these repositories)
        // For now, we'll return the stored PDF
        throw new UnsupportedOperationException("Use downloadInvoicePdf instead");
    }

    @Override
    public Invoice getInvoiceByOrderId(String orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "orderId", orderId));
    }

    @Override
    public Invoice getInvoiceById(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllByOrderByInvoiceDateDesc();
    }

    @Override
    public byte[] downloadInvoicePdf(String invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);
        
        try {
            // Download from R2/S3
            return storageService.downloadFile(invoice.getPdfPath());
        } catch (Exception e) {
            log.error("Failed to download invoice PDF: {}", invoiceId, e);
            throw new RuntimeException("Failed to download invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public ByteArrayOutputStream downloadAllInvoicesZip() {
        try {
            List<Invoice> invoices = getAllInvoices();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (Invoice invoice : invoices) {
                try {
                    byte[] pdfData = downloadInvoicePdf(invoice.getId());
                    ZipEntry entry = new ZipEntry(invoice.getInvoiceNumber() + ".pdf");
                    zos.putNextEntry(entry);
                    zos.write(pdfData);
                    zos.closeEntry();
                } catch (Exception e) {
                    log.error("Failed to add invoice to zip: {}", invoice.getInvoiceNumber(), e);
                }
            }

            zos.close();
            return baos;

        } catch (Exception e) {
            log.error("Failed to create invoices zip", e);
            throw new RuntimeException("Failed to create invoices zip: " + e.getMessage(), e);
        }
    }

    private ByteArrayOutputStream generateInvoicePdfInternal(Order order, User user, Invoice invoice) throws Exception {
        // Load JRXML template
        InputStream templateStream = new ClassPathResource("invoice_template.jrxml").getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        // Load logo image
        InputStream logoStream = new ClassPathResource("images/logo.png").getInputStream();

        // Prepare parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("invoiceNumber", invoice.getInvoiceNumber());
        parameters.put("invoiceDate", invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        parameters.put("customerName", user.getFullName());
        parameters.put("customerEmail", user.getEmail());
        parameters.put("customerAddress", formatAddress(order.getAddress()));
        parameters.put("orderId", order.getId());
        parameters.put("subtotal", invoice.getSubtotal());
        parameters.put("taxAmount", invoice.getTaxAmount());
        parameters.put("totalAmount", invoice.getTotalAmount());
        parameters.put("logoPath", logoStream);

        // Prepare data source
        List<InvoiceItem> items = new ArrayList<>();
        for (Order.OrderItem orderItem : order.getItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setProductId(orderItem.getProductId());
            item.setQuantity(orderItem.getQuantity());
            item.setPrice(orderItem.getPrice());
            item.setTotal(orderItem.getPrice() * orderItem.getQuantity());
            items.add(item);
        }

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);

        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export to PDF
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        return outputStream;
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return prefix + "-" + timestamp + "-" + random;
    }

    private String formatAddress(Order.Address address) {
        return address.getAddressLine() + ", " +
               address.getCity() + ", " +
               address.getState() + " " +
               address.getPostalCode() + ", " +
               address.getCountry();
    }

    // Inner class for invoice items
    public static class InvoiceItem {
        private String productId;
        private Integer quantity;
        private Double price;
        private Double total;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
    }
}
