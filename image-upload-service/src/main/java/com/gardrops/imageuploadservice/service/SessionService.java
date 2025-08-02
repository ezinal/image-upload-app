package com.gardrops.imageuploadservice.service;

import com.gardrops.imageuploadservice.model.Session;

import java.util.UUID;

public interface SessionService {
    UUID createSession();
    Session getSession(UUID sessionId);
    void addImageToSession(UUID sessionId, UUID imageId);
    boolean canAddImage(UUID sessionId);
    boolean isSessionValid(UUID sessionId);
    boolean hasImage(UUID sessionId, UUID imageId);
    void removeImageFromSession(UUID sessionId, UUID imageId);
}
