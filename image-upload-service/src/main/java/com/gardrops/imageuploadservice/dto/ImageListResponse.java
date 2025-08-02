package com.gardrops.imageuploadservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ImageListResponse {
    @NotNull(message = "Image IDs list cannot be empty")
    private List<UUID> imageIds;
}
