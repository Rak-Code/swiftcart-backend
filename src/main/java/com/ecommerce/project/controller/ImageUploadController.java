package com.ecommerce.project.controller;

import com.ecommerce.project.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image Upload", description = "Image upload and management endpoints")
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/upload")
    @Operation(summary = "Upload a single image", description = "Upload a single image to Cloudflare R2 storage")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "products") String folder) {
        
        try {
            String imageUrl = imageStorageService.uploadImage(file, folder);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image uploaded successfully");
            response.put("imageUrl", imageUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("Error uploading image: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/upload-multiple")
    @Operation(summary = "Upload multiple images", description = "Upload multiple images to Cloudflare R2 storage")
    public ResponseEntity<Map<String, Object>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folder", required = false, defaultValue = "products") String folder) {
        
        try {
            List<String> imageUrls = imageStorageService.uploadImages(files, folder);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Images uploaded successfully");
            response.put("imageUrls", imageUrls);
            response.put("count", imageUrls.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading images: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to upload images");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete an image", description = "Delete an image from Cloudflare R2 storage")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            boolean deleted = imageStorageService.deleteImage(imageUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", deleted);
            response.put("message", deleted ? "Image deleted successfully" : "Failed to delete image");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting image: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete-multiple")
    @Operation(summary = "Delete multiple images", description = "Delete multiple images from Cloudflare R2 storage")
    public ResponseEntity<Map<String, Object>> deleteMultipleImages(@RequestBody List<String> imageUrls) {
        try {
            boolean allDeleted = imageStorageService.deleteImages(imageUrls);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", allDeleted);
            response.put("message", allDeleted ? "All images deleted successfully" : "Some images failed to delete");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting images: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete images");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
