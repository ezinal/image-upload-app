package com.gardrops.imageuploadservice.controller;

import com.gardrops.imageuploadservice.dto.ImageListResponse;
import com.gardrops.imageuploadservice.dto.SessionResponse;
import com.gardrops.imageuploadservice.service.SessionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession() {
        UUID sessionId = sessionService.createSession();
        return ResponseEntity.ok(new SessionResponse(sessionId));
    }

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

        UUID imageId = UUID.randomUUID();

        // TODO: call image process service here

        sessionService.addImageToSession(sessionId, imageId);

        return ResponseEntity.ok(imageId);
    }

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

        // TODO: call image process service here

        sessionService.removeImageFromSession(sessionId, imageId);

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
