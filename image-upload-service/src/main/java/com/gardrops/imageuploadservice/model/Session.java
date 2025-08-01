package com.gardrops.imageuploadservice.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID sessionId;
    private LocalDateTime createdAt;
    private Set<UUID> imageIds;

    public Session(UUID sessionId) {
        this.sessionId = sessionId;
        this.createdAt = LocalDateTime.now();
        this.imageIds = new HashSet<>();
    }

    public void addImage(UUID imageId) {
        this.imageIds.add(imageId);
    }

    public void removeImage(UUID imageId) {
        this.imageIds.remove(imageId);
    }

    public boolean hasImage(UUID imageId) {
        return this.imageIds.contains(imageId);
    }
}
