package com.gardrops.imageuploadservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ImageListResponse {
    private List<UUID> imageIds;
}
