package com.gardrops.imageuploadservice.service;

import com.gardrops.imageuploadservice.model.Session;
import com.gardrops.imageuploadservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    @Value("${app.max-images-per-session}")
    private Long maxImagesPerSession;

    private final SessionRepository sessionRepository;

    public UUID createSession() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId);
        sessionRepository.saveSession(session);
        return sessionId;
    }

    public Session getSession(UUID sessionId) {
        return sessionRepository.findSession(sessionId);
    }

    public void addImageToSession(UUID sessionId, UUID imageId) {
        Session session = sessionRepository.findSession(sessionId);
        if (session != null) {
            session.addImage(imageId);
            sessionRepository.saveSession(session);
        }
    }

    public boolean canAddImage(UUID sessionId) {
        Session session = sessionRepository.findSession(sessionId);
        if (session == null) {
            return false;
        }
        return session.getImageIds().size() < maxImagesPerSession;
    }

    public boolean isSessionValid(UUID sessionId) {
        return sessionRepository.exists(sessionId);
    }

    public boolean hasImage(UUID sessionId, UUID imageId) {
        Session session = sessionRepository.findSession(sessionId);
        return session != null && session.hasImage(imageId);
    }

    public void removeImageFromSession(UUID sessionId, UUID imageId) {
        Session session = sessionRepository.findSession(sessionId);
        if (session != null) {
            session.removeImage(imageId);
            sessionRepository.saveSession(session);
        }
    }
}
