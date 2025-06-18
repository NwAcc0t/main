package com.example.facade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MessageController {

    private final LoggingClient loggingClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${messages.instance}")
    private String messagesInstanceUrl;

    public MessageController(LoggingClient loggingClient) {
        this.loggingClient = loggingClient;
    }

    @PostMapping("/messages")
    public void postMessage(@RequestBody Message message) {
        loggingClient.postMessage(message);
    }

    @GetMapping("/messages")
    public Map<String, Object> getMessages() {
        String staticText = restTemplate.getForObject(messagesInstanceUrl, String.class);
        List<String> logs = loggingClient.getAllMessages();

        Map<String, Object> result = new HashMap<>();
        result.put("static", staticText);
        result.put("logs", logs);
        return result;
    }
}
