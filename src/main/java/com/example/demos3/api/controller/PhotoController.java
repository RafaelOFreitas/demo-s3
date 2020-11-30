package com.example.demos3.api.controller;

import com.example.demos3.api.dto.PhotoInput;
import com.example.demos3.api.dto.PhotoOutput;
import com.example.demos3.api.mapper.PhotoMapper;
import com.example.demos3.domain.model.Photo;
import com.example.demos3.domain.service.PhotoCatalogService;
import com.example.demos3.domain.service.PhotoStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoMapper photoMapper;
    private final PhotoCatalogService photoCatalogService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoOutput> savePhoto(@Valid PhotoInput photoInput) throws IOException {
        var file = photoInput.getFile();

        Photo photo = Photo.builder()
                .fileName(file.getOriginalFilename())
                .description(photoInput.getDescription())
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        photo = this.photoCatalogService.savePhoto(photo, file.getInputStream());

        return ResponseEntity.ok().body(this.photoMapper.toOutput(photo));
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> toRecoverProductPhoto(@PathVariable String fileName) {
        PhotoStorageService.PhotoRecover photoRecover = this.photoCatalogService.recover(fileName);

        if (photoRecover.hasUrl()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, photoRecover.getUrl())
                    .build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteProductPhoto(@PathVariable String fileName) {
        this.photoCatalogService.deletePhotoByFileName(fileName);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
