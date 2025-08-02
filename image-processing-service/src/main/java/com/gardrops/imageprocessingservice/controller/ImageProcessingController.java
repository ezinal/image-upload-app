package com.gardrops.imageprocessingservice.controller;

import com.gardrops.imageprocessingservice.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageProcessingController {

    private final ImageProcessingService imageProcessingService;

    @PostMapping("/process")
    public ResponseEntity<Void> processImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("destinationFilePath") String destinationFilePath) {

        try {
            imageProcessingService.processAndStoreImage(image, destinationFilePath, sessionId);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{sessionId}/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable String sessionId,
            @PathVariable String imageId) {
        try {
            imageProcessingService.deleteImage(sessionId, imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
