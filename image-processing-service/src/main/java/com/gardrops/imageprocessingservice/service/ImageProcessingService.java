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
        String basePath = String.format("%s/%s/", baseFolder, sessionId);
        String destinationFilePath = basePath + imageName;

        Path destinationPath = Paths.get(destinationFilePath);
        Path parentDir = destinationPath.getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }

        return destinationPath.toFile();
    }
}