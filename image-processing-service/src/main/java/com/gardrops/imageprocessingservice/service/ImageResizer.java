package com.gardrops.imageprocessingservice.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ImageResizer {
    void resizeAndWriteJPEG(InputStream inputFile, File outputFile, int width, int height, float quality, FileStorageService fileStorageService) throws IOException;
}
