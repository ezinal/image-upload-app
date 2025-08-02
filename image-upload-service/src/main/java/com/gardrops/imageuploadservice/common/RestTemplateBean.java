package com.gardrops.imageuploadservice.common;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateBean {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
