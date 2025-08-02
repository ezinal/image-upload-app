package com.gardrops.imageprocessingservice.service.impl;

import com.gardrops.imageprocessingservice.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.image-properties.base-folder}")
    private String baseFolder;

    @Override
    public File createOutputFile(String imageName, String sessionId) throws IOException {
        String destinationFilePath = getDestinationFilePath(sessionId, imageName);

        Path destinationPath = Paths.get(destinationFilePath);
        Path parentDir = destinationPath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        return destinationPath.toFile();
    }

    @Override
    public void deleteImage(String sessionId, String imageId) throws IOException {
        String imagePath = getDestinationFilePath(sessionId, imageId + ".jpg");
        Path path = Paths.get(imagePath);
        Files.deleteIfExists(path);
    }

    @Override
    public String getDestinationFilePath(String sessionId, String imageName) {
        String basePath = String.format("%s/%s/", baseFolder, sessionId);
        return basePath + imageName;
    }
    
    @Override
    public void writeImageAsJPEG(BufferedImage image, File outputFile, float quality) throws IOException {
        // Get JPEG writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) throw new IllegalStateException("No writers found for jpeg");
        ImageWriter writer = writers.next();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(ios);

            // Set JPEG compression quality
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionType("JPEG");
                param.setCompressionQuality(quality); // 0.0 to 1.0
            }

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }
    }
    
    @Override
    public BufferedImage readImageFromStream(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }
} 