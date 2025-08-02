package com.gardrops.imageuploadservice.controller;

import com.gardrops.imageuploadservice.dto.ImageListResponse;
import com.gardrops.imageuploadservice.dto.SessionResponse;
import com.gardrops.imageuploadservice.exception.ImageNotFoundException;
import com.gardrops.imageuploadservice.exception.InvalidFileTypeException;
import com.gardrops.imageuploadservice.exception.SessionLimitExceededException;
import com.gardrops.imageuploadservice.exception.SessionNotFoundException;
import com.gardrops.imageuploadservice.service.ImageProcessingServiceClient;
import com.gardrops.imageuploadservice.service.SessionService;
import com.gardrops.imageuploadservice.validation.FileValidationUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/sessions")
@Validated
@Slf4j
public class SessionController {

    private final SessionService sessionService;
    private final ImageProcessingServiceClient imageProcessingClient;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession() {
        log.info("Creating new session");
        UUID sessionId = sessionService.createSession();
        log.info("Session created with ID: {}", sessionId);
        return ResponseEntity.ok(new SessionResponse(sessionId));
    }

    @PostMapping("/{sessionId}/images")
    public ResponseEntity<UUID> uploadImage(
            @PathVariable @NotNull(message = "Session ID cannot be null") UUID sessionId,
            @RequestParam("image") @NotNull(message = "Image file cannot be null") MultipartFile image) {

        log.info("Uploading image for session: {}", sessionId);
        
        // Validate file type and format
        try {
            FileValidationUtils.validateImageFile(image);
        } catch (InvalidFileTypeException e) {
            log.warn("Invalid file type for session {}: {}", sessionId, e.getMessage());
            throw e;
        }

        // Validate session exists
        if (!sessionService.isSessionValid(sessionId)) {
            log.warn("Session not found: {}", sessionId);
            throw new SessionNotFoundException("Session not found: " + sessionId);
        }

        if (!sessionService.canAddImage(sessionId)) {
            log.warn("Session limit exceeded for session: {}", sessionId);
            throw new SessionLimitExceededException("Session has reached maximum image limit");
        }

        try {
            UUID imageId = UUID.randomUUID();
            log.info("Processing image {} for session {}", imageId, sessionId);

            imageProcessingClient.processAndStoreImage(image, sessionId, imageId);

            sessionService.addImageToSession(sessionId, imageId); // only store images to session if processing is successful
            log.info("Image {} successfully uploaded to session {}", imageId, sessionId);

            return ResponseEntity.ok(imageId);
        } catch (IOException e) {
            log.error("Error processing image for session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{sessionId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable @NotNull(message = "Session ID cannot be null") UUID sessionId,
            @PathVariable @NotNull(message = "Image ID cannot be null") UUID imageId) {

        log.info("Deleting image {} from session {}", imageId, sessionId);

        if (!sessionService.isSessionValid(sessionId)) {
            log.warn("Session not found: {}", sessionId);
            throw new SessionNotFoundException("Session not found: " + sessionId);
        }

        if (!sessionService.hasImage(sessionId, imageId)) {
            log.warn("Image {} not found in session {}", imageId, sessionId);
            throw new ImageNotFoundException("Image not found in session: " + imageId);
        }

        try {
            imageProcessingClient.deleteImage(sessionId, imageId);
            sessionService.removeImageFromSession(sessionId, imageId);
            log.info("Image {} successfully deleted from session {}", imageId, sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting image {} from session {}: {}", imageId, sessionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{sessionId}/images")
    public ResponseEntity<ImageListResponse> listImages(
            @PathVariable @NotNull(message = "Session ID cannot be null") UUID sessionId) {

        log.info("Listing images for session: {}", sessionId);

        if (!sessionService.isSessionValid(sessionId)) {
            log.warn("Session not found: {}", sessionId);
            throw new SessionNotFoundException("Session not found: " + sessionId);
        }

        var session = sessionService.getSession(sessionId);
        List<UUID> imageIds = session.getImageIds().stream().toList();
        log.info("Found {} images in session {}", imageIds.size(), sessionId);

        return ResponseEntity.ok(new ImageListResponse(imageIds));
    }
}
