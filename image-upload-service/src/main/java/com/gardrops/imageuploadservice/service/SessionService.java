package com.gardrops.imageuploadservice.service;

import com.gardrops.imageuploadservice.model.Session;
import com.gardrops.imageuploadservice.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public UUID createSession() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId);
        sessionRepository.saveSession(session);
        return sessionId;
    }
}
