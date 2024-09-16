package com.gtasterix.ImageUpload.service;


import com.gtasterix.ImageUpload.exception.FileSizeExceededException;

import com.gtasterix.ImageUpload.exception.ImageNotFoundException;
import com.gtasterix.ImageUpload.model.Image;
import com.gtasterix.ImageUpload.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ImageStorageService {

    @Autowired
    private ImageRepository imageRepository;

    private static final String IMAGE_STORAGE_PATH = "images/";

    // Ensure the image storage directory exists
    static {
        File directory = new File(IMAGE_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // Upload image with size limit
    public Image uploadImageWithSizeLimit(MultipartFile file, String fileDownloadUri, long sizeLimit) throws IOException {
        if (file.getSize() > sizeLimit) {
            throw new FileSizeExceededException("File size exceeds the limit of " + sizeLimit / (1024 * 1024) + " MB");
        }
        return saveImage(file, fileDownloadUri);
    }

    // Upload image without size limit
    public Image uploadImageWithoutSizeLimit(MultipartFile file, String fileDownloadUri) throws IOException {
        return saveImage(file, fileDownloadUri);
    }

    // Save image to the local file system and database
    private Image saveImage(MultipartFile file, String fileDownloadUri) throws IOException {
        String filename = file.getOriginalFilename();
        File localFile = new File(IMAGE_STORAGE_PATH + filename);

        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            fos.write(file.getBytes());
        }

        Image imageEntity = new Image(filename, fileDownloadUri, localFile.getAbsolutePath());
        return imageRepository.save(imageEntity);
    }

    // Get image by ID
    public Optional<Image> getImage(Long id) {
        return imageRepository.findById(id);
    }

    // Get all images
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    // Update image (replace image data)
    public Image updateImage(Long id, MultipartFile file) throws IOException {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (!imageOptional.isPresent()) {
            throw new ImageNotFoundException("Image with ID " + id + " not found");
        }

        Image image = imageOptional.get();
        File localFile = new File(image.getFilePath());
        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            fos.write(file.getBytes());
        }

        return imageRepository.save(image);
    }

    // Delete image by ID
    public void deleteImage(Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (!imageOptional.isPresent()) {
            throw new ImageNotFoundException("Image with ID " + id + " not found");
        }

        Image image = imageOptional.get();
        File localFile = new File(image.getFilePath());
        if (localFile.exists()) {
            localFile.delete();
        }
        imageRepository.deleteById(id);
    }
}
