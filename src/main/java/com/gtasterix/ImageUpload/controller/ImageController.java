package com.gtasterix.ImageUpload.controller;

import com.gtasterix.ImageUpload.exception.FileSizeExceededException;
import com.gtasterix.ImageUpload.exception.ImageNotFoundException;
import com.gtasterix.ImageUpload.model.Image;
import com.gtasterix.ImageUpload.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageStorageService imageStorageService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Upload image with size limit (5MB)
    @PostMapping("/upload/limited")
    public ResponseEntity<Image> uploadImageWithLimit(@RequestParam("file") MultipartFile file) {
        try {
            String fileDownloadUri = "/api/images/view/filename/" + file.getOriginalFilename();
            Image savedImage = imageStorageService.uploadImageWithSizeLimit(file, fileDownloadUri, MAX_FILE_SIZE);
            return ResponseEntity.created(URI.create(fileDownloadUri)).body(savedImage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (FileSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(null);
        }
    }

    // Upload image without size limit
    @PostMapping("/upload/unlimited")
    public ResponseEntity<Image> uploadImageWithoutLimit(@RequestParam("file") MultipartFile file) {
        try {
            String fileDownloadUri = "/api/images/view/filename/" + file.getOriginalFilename();
            Image savedImage = imageStorageService.uploadImageWithoutSizeLimit(file, fileDownloadUri);
            return ResponseEntity.created(URI.create(fileDownloadUri)).body(savedImage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // View image by ID
    @GetMapping("/view/id/{id}")
    public ResponseEntity<byte[]> viewImageById(@PathVariable Long id) {
        Optional<Image> imageOptional = imageStorageService.getImage(id);

        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg"); // Adjust based on image type
            return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);  // return the image data
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // View image by filename
    @GetMapping("/view/filename/{filename}")
    public ResponseEntity<byte[]> viewImageByFilename(@PathVariable String filename) {
        Optional<Image> imageOptional = imageStorageService.getImageByName(filename);

        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg"); // Adjust based on image type
            return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);  // return the image data
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all images
    @GetMapping
    public ResponseEntity<List<Image>> getAllImages() {
        List<Image> images = imageStorageService.getAllImages();
        return ResponseEntity.ok(images);
    }

    // Get image details (excluding binary data) by ID
    @GetMapping("/{id}")
    public ResponseEntity<Image> getImageDetails(@PathVariable Long id) {
        Optional<Image> imageOptional = imageStorageService.getImage(id);
        if (imageOptional.isPresent()) {
            return ResponseEntity.ok(imageOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update image by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Image updatedImage = imageStorageService.updateImage(id, file);
            return ResponseEntity.ok(updatedImage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (ImageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Delete image by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        try {
            imageStorageService.deleteImage(id);
            return ResponseEntity.noContent().build();
        } catch (ImageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
