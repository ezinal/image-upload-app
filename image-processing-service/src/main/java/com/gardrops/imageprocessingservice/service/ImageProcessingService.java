package com.gardrops.imageprocessingservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageProcessingService {

    void processAndStoreImage(MultipartFile image, String imageName, String sessionId) throws IOException;

    void deleteImage(String sessionId, String imageId) throws IOException;

}
