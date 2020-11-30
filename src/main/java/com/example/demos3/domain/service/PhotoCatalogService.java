package com.example.demos3.domain.service;

import com.example.demos3.domain.model.Photo;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class PhotoCatalogService {
    private final PhotoStorageService photoStorageService;

    public PhotoCatalogService(PhotoStorageService photoStorageService) {
        this.photoStorageService = photoStorageService;
    }

    public Photo savePhoto(Photo productPhoto, InputStream fileData) {
        String fileName = this.photoStorageService.getGenerateFileName(productPhoto.getFileName());
        productPhoto.setFileName(fileName);

        var newPicture = PhotoStorageService.NewPicture.builder()
                .fileName(productPhoto.getFileName())
                .contentType(productPhoto.getContentType())
                .inputStream(fileData)
                .build();

        this.photoStorageService.save(newPicture);

        return productPhoto;
    }

    public PhotoStorageService.PhotoRecover recover(String fileName) {
        return this.photoStorageService.toRecover(fileName);
    }

    public void deletePhotoByFileName(String fileName) {
        this.photoStorageService.delete(fileName);
    }
}
