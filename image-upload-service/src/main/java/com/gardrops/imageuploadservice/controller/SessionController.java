package com.gardrops.imageuploadservice.controller;

import com.gardrops.imageuploadservice.dto.ImageListResponse;
import com.gardrops.imageuploadservice.dto.SessionResponse;
import com.gardrops.imageuploadservice.service.ImageProcessingServiceClient;
import com.gardrops.imageuploadservice.service.SessionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final ImageProcessingServiceClient imageProcessingClient;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession() {
        UUID sessionId = sessionService.createSession();
        return ResponseEntity.ok(new SessionResponse(sessionId));
    }

    // TODO ADD VALIDATIONS !!!!
    @PostMapping("/{sessionId}/images")
    public ResponseEntity<UUID> uploadImage(
            @PathVariable UUID sessionId,
            @RequestParam("image") MultipartFile image) {

        if (!sessionService.isSessionValid(sessionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!sessionService.canAddImage(sessionId)) {
            // TODO: add generic exception handler and exception types
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            UUID imageId = UUID.randomUUID();

            imageProcessingClient.processAndStoreImage(image, sessionId, imageId);

            sessionService.addImageToSession(sessionId, imageId); // only store images to session if processing is successful

            return ResponseEntity.ok(imageId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // TODO ADD VALIDATIONS !!!!
    @DeleteMapping("/{sessionId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID sessionId,
            @PathVariable UUID imageId) {

        if (!sessionService.isSessionValid(sessionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (!sessionService.hasImage(sessionId, imageId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        imageProcessingClient.deleteImage(sessionId, imageId);

        sessionService.removeImageFromSession(sessionId, imageId); // remove from session if only image gets deleted from storage

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/images")
    public ResponseEntity<ImageListResponse> listImages(@PathVariable UUID sessionId) {

        if (!sessionService.isSessionValid(sessionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var session = sessionService.getSession(sessionId);
        List<UUID> imageIds = session.getImageIds().stream().toList();

        return ResponseEntity.ok(new ImageListResponse(imageIds));
    }
}
