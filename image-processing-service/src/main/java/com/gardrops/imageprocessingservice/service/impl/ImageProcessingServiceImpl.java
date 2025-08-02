package com.gardrops.imageprocessingservice.service.impl;

import com.gardrops.imageprocessingservice.service.FileStorageService;
import com.gardrops.imageprocessingservice.service.ImageProcessingService;
import com.gardrops.imageprocessingservice.service.ImageResizer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageProcessingServiceImpl implements ImageProcessingService {

    @Value("${app.image-properties.max-width}")
    private int maxWidth;

    @Value("${app.image-properties.max-height}")
    private int maxHeight;

    @Value("${app.image-properties.jpeg-quality}")
    private float jpegQuality;

    private final FileStorageService fileStorageService;

    @Override
    public void processAndStoreImage(MultipartFile image, String imageName, String sessionId) throws IOException {
        BufferedImage originalImage = fileStorageService.readImageFromStream(image.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image format");
        }

        ImageResizer imageResizer = new Java2DImageResizer();

        File outputFile = fileStorageService.createOutputFile(imageName, sessionId);

        imageResizer.resizeAndWriteJPEG(image.getInputStream(), outputFile, maxWidth, maxHeight, jpegQuality, fileStorageService);
    }

    @Override
    public void deleteImage(String sessionId, String imageId) throws IOException {
        fileStorageService.deleteImage(sessionId, imageId);
    }
}