package com.gardrops.imageuploadservice.controller;

import com.gardrops.imageuploadservice.dto.SessionResponse;
import com.gardrops.imageuploadservice.service.SessionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
