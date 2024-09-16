package com.gtasterix.ImageUpload.service;

import com.gtasterix.ImageUpload.exception.FileSizeExceededException;
import com.gtasterix.ImageUpload.exception.ImageNotFoundException;
import com.gtasterix.ImageUpload.model.Image;
import com.gtasterix.ImageUpload.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageStorageService {

    @Autowired
    private ImageRepository imageRepository;

    // Upload image with size limit
    public Image uploadImageWithSizeLimit(MultipartFile file, String url, long maxFileSize) throws IOException {
        if (file.getSize() > maxFileSize) {
            throw new FileSizeExceededException("File size exceeds the limit of 5MB");
        }

        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setUrl(url);
        image.setImage(file.getBytes());  // Store image as binary data

        return imageRepository.save(image);
    }

    // Upload image without size limit
    public Image uploadImageWithoutSizeLimit(MultipartFile file, String url) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setUrl(url);
        image.setImage(file.getBytes());  // Store image as binary data

        return imageRepository.save(image);
    }

    // Get image by ID
    public Optional<Image> getImage(Long id) {
        return imageRepository.findById(id);
    }

    public Optional<Image> getImageByName(String filename) {
        return imageRepository.findByName(filename);  // Assuming `findByName` is implemented in repository
    }


    // Get all images
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    // Update image by ID
    public Image updateImage(Long id, MultipartFile file) throws IOException {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            image.setName(file.getOriginalFilename());
            image.setImage(file.getBytes());  // Update the image binary data
            return imageRepository.save(image);
        } else {
            throw new ImageNotFoundException("Image not found with id " + id);
        }
    }

    // Delete image by ID
    public void deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new ImageNotFoundException("Image not found with id " + id);
        }
    }
}
