package com.gardrops.imageprocessingservice.service.impl;

import com.gardrops.imageprocessingservice.service.FileStorageService;
import com.gardrops.imageprocessingservice.service.ImageResizer;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class Java2DImageResizer implements ImageResizer {

    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Calculate new dimensions maintaining aspect ratio
        double scaleX = (double) maxWidth / originalWidth;
        double scaleY = (double) maxHeight / originalHeight;
        double scale = Math.min(scaleX, scaleY);

        // If image is already smaller than max dimensions, don't resize
        if (scale >= 1.0) {
            return originalImage;
        }

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    public void resizeAndWriteJPEG(InputStream inputFile, File outputFile, int maxWidth, int maxHeight, float quality, FileStorageService fileStorageService) throws IOException {
        BufferedImage originalImage = fileStorageService.readImageFromStream(inputFile);

        BufferedImage resizedImage = resizeImage(originalImage, maxWidth, maxHeight);

        fileStorageService.writeImageAsJPEG(resizedImage, outputFile, quality);
    }
}
