package com.gardrops.imageuploadservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageProcessingServiceClient {

    @Value("${app.processing-service.url}")
    private String processingServiceUrl;

    @Value("${app.processing-service.image-process-url}")
    private String imageProcessEndpointUrl;

    @Value("${app.processing-service.image-delete-url}")
    private String imageDeleteEndpointUrl;

    private final RestTemplate restTemplate;

    public void processAndStoreImage(MultipartFile image, UUID sessionId, UUID imageId) throws IOException {
        var destinationPath = String.format("%s.jpg", imageId);

        var requestEntity = createImageProcessRequestEntity(image, sessionId, destinationPath);

        var url = processingServiceUrl + imageProcessEndpointUrl;
        var response = restTemplate.postForEntity(
                url,
                requestEntity,
                Void.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to process image: " + response.getStatusCode());
        }
    }

    private static HttpEntity<MultiValueMap<String, Object>> createImageProcessRequestEntity(MultipartFile image, UUID sessionId, String destinationPath) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });
        body.add("sessionId", sessionId.toString());
        body.add("destinationFilePath", destinationPath);

        return new HttpEntity<>(body, headers);
    }

    public void deleteImage(UUID sessionId, UUID imageId) {
        try {
            var url = String.format("%s/%s/%s", processingServiceUrl + imageDeleteEndpointUrl, sessionId, imageId);

            restTemplate.delete(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}