package com.gtasterix.ImageUpload.repository;


import com.gtasterix.ImageUpload.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}
