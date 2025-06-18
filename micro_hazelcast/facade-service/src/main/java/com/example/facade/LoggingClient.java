package com.example.facade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class LoggingClient {

    private final List<String> urls;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    // Розщеплюємо рядок на список по комі
    public LoggingClient(@Value("#{'${logging.instances}'.split(',')}") List<String> urls) {
        this.urls = urls;
    }

    public void postMessage(Message msg) {
        String url = urls.get(random.nextInt(urls.size()));
        restTemplate.postForEntity(url, msg, Void.class);
    }

    public List<String> getAllMessages() {
        String url = urls.get(random.nextInt(urls.size()));
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        ).getBody();
    }
}
