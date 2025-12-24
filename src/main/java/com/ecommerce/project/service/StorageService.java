package com.ecommerce.project.service;

public interface StorageService {
    
    /**
     * Upload a file (byte array) to R2 storage
     * @param data The file data as byte array
     * @param fileName The file name/path (e.g., "invoices/INV-123.pdf")
     * @param contentType The MIME type (e.g., "application/pdf")
     * @return Public URL of the uploaded file
     */
    String uploadFile(byte[] data, String fileName, String contentType);
    
    /**
     * Download a file from R2 storage
     * @param fileUrl The public URL or file path
     * @return File data as byte array
     */
    byte[] downloadFile(String fileUrl);
    
    /**
     * Delete a file from R2 storage
     * @param fileUrl The public URL of the file to delete
     * @return true if deleted successfully
     */
    boolean deleteFile(String fileUrl);
}
