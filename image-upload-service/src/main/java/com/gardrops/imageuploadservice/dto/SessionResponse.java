package com.gardrops.imageuploadservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SessionResponse {
    @NotNull(message = "Session ID cannot be empty")
    private UUID sessionId;
}
