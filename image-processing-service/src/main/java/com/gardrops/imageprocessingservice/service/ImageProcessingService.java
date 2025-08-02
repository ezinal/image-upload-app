package com.gardrops.imageprocessingservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageProcessingService {

    @Value("${app.image-properties.max-width}")
    private int maxWidth;

    @Value("${app.image-properties.max-height}")
    private int maxHeight;

    @Value("${app.image-properties.jpeg-quality}")
    private float jpegQuality;

    @Value( "${app.image-properties.base-folder}")
    private String baseFolder;

    public void processAndStoreImage(MultipartFile image, String imageName, String sessionId) throws IOException {
        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        if (originalImage == null) {
            throw new IOException("Invalid image format");
        }

        ImageResizer imageResizer = new Java2DImageResizer();

        File outputFile = getOutputFile(imageName, sessionId);

        imageResizer.resizeAndWriteJPEG(image.getInputStream(), outputFile, maxWidth, maxHeight, jpegQuality);
    }

    private File getOutputFile(String imageName, String sessionId) throws IOException {
        String destinationFilePath = getDestinationFilePath(sessionId, imageName);

        Path destinationPath = Paths.get(destinationFilePath);
        Path parentDir = destinationPath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        return destinationPath.toFile();
    }

    // TODO move file store operations to antoher class and create a strategy interface
    private String getDestinationFilePath(String sessionId, String imageName) {
        String basePath = String.format("%s/%s/", baseFolder, sessionId);
        return basePath + imageName;
    }

    public void deleteImage(String sessionId, String imageId) throws IOException {
        String imagePath = getDestinationFilePath(sessionId, imageId + ".jpg");
        Path path = Paths.get(imagePath);
        Files.deleteIfExists(path);
    }
}