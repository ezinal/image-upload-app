package com.gardrops.imageuploadservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ImageProcessingServiceClient {
    void processAndStoreImage(MultipartFile image, UUID sessionId, UUID imageId) throws IOException;

    void deleteImage(UUID sessionId, UUID imageId);

}
