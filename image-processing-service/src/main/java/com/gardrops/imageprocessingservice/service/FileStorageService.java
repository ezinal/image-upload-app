package com.gardrops.imageprocessingservice.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileStorageService {
    
    /**
     * Creates a file for the given session and image name
     * @param imageName the name of the image file
     * @param sessionId the session identifier
     * @return the File object representing the output file
     * @throws IOException if there's an error creating the file or directories
     */
    File createOutputFile(String imageName, String sessionId) throws IOException;
    
    /**
     * Deletes an image file for the given session and image ID
     * @param sessionId the session identifier
     * @param imageId the image identifier
     * @throws IOException if there's an error deleting the file
     */
    void deleteImage(String sessionId, String imageId) throws IOException;
    
    /**
     * Gets the destination file path for a given session and image name
     * @param sessionId the session identifier
     * @param imageName the name of the image file
     * @return the file path as a string
     */
    String getDestinationFilePath(String sessionId, String imageName);
    
    /**
     * Writes a BufferedImage to a file as JPEG with specified quality
     * @param image the BufferedImage to write
     * @param outputFile the target file
     * @param quality the JPEG quality (0.0 to 1.0)
     * @throws IOException if there's an error writing the file
     */
    void writeImageAsJPEG(BufferedImage image, File outputFile, float quality) throws IOException;
    
    /**
     * Reads an image from an InputStream
     * @param inputStream the input stream containing the image data
     * @return the BufferedImage read from the stream
     * @throws IOException if there's an error reading the image
     */
    BufferedImage readImageFromStream(InputStream inputStream) throws IOException;
} 