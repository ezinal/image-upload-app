package com.gardrops.imageuploadservice.service.impl;

import com.gardrops.imageuploadservice.model.Session;
import com.gardrops.imageuploadservice.repository.SessionRepository;
import com.gardrops.imageuploadservice.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    @Value("${app.max-images-per-session}")
    private Long maxImagesPerSession;

    private final SessionRepository sessionRepository;

    @Override
    public UUID createSession() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId);
        sessionRepository.saveSession(session);
        return sessionId;
    }

    @Override
    public Session getSession(UUID sessionId) {
        return sessionRepository.findSession(sessionId);
    }

    @Override
    public void addImageToSession(UUID sessionId, UUID imageId) {
        Session session = sessionRepository.findSession(sessionId);
        if (session != null) {
            session.addImage(imageId);
            sessionRepository.saveSession(session);
        }
    }

    @Override
    public boolean canAddImage(UUID sessionId) {
        Session session = sessionRepository.findSession(sessionId);
        if (session == null) {
            return false;
        }
        return session.getImageIds().size() < maxImagesPerSession;
    }

    @Override
    public boolean isSessionValid(UUID sessionId) {
        return sessionRepository.exists(sessionId);
    }

    @Override
    public boolean hasImage(UUID sessionId, UUID imageId) {
        Session session = sessionRepository.findSession(sessionId);
        return session != null && session.hasImage(imageId);
    }

    @Override
    public void removeImageFromSession(UUID sessionId, UUID imageId) {
        Session session = sessionRepository.findSession(sessionId);
        if (session != null) {
            session.removeImage(imageId);
            sessionRepository.saveSession(session);
        }
    }
}
