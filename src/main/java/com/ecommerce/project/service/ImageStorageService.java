package com.ecommerce.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {
    
    /**
     * Upload a single image to R2 storage
     * @param file The image file to upload
     * @param folder Optional folder path (e.g., "products", "categories")
     * @return Public URL of the uploaded image
     */
    String uploadImage(MultipartFile file, String folder);
    
    /**
     * Upload multiple images to R2 storage
     * @param files List of image files to upload
     * @param folder Optional folder path
     * @return List of public URLs of uploaded images
     */
    List<String> uploadImages(List<MultipartFile> files, String folder);
    
    /**
     * Delete an image from R2 storage
     * @param imageUrl The public URL of the image to delete
     * @return true if deleted successfully
     */
    boolean deleteImage(String imageUrl);
    
    /**
     * Delete multiple images from R2 storage
     * @param imageUrls List of public URLs to delete
     * @return true if all deleted successfully
     */
    boolean deleteImages(List<String> imageUrls);
}
