package com.example.facade.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/facade")
public class FacadeController {

    private final Logger log = LoggerFactory.getLogger(FacadeController.class);

    /** Список адрес (host:port) нод logging-service, з яких випадково обиратимемо одну */
    @Value("${logging.instances}")
    private String loggingInstancesProperty;

    private List<String> loggingInstances;  // список у форматі ["localhost:9901", "localhost:9902", ...]

    private final WebClient webClient = WebClient.builder()
            .build();

    @PostConstruct
    public void init() {
        loggingInstances = Arrays.asList(loggingInstancesProperty.split(","));
    }

    /** DTO для запиту POST /api/facade/log */
    public static class FacadePostRequest {
        @JsonProperty("msg")
        public String msg;
    }

    /** DTO для передачі до logging-service: { "id": <uuid>, "msg": "<текст>" } */
    public static class LoggingRequest {
        @JsonProperty("id")
        public UUID id;

        @JsonProperty("msg")
        public String msg;

        public LoggingRequest(UUID id, String msg) {
            this.id = id;
            this.msg = msg;
        }
    }

    /** 
     * POST /api/facade/log 
     *  – Приймає { "msg": "<текст>" }, створює UUID, формує LoggingRequest 
     *  – Випадково обирає одну адресу із loggingInstances і виконує клієнтський POST 
     *  – Повертає клієнтові HTTP 200 OK (або помилку, якщо не вдалося). 
     */
    @PostMapping("/log")
    public ResponseEntity<String> postLog(@RequestBody FacadePostRequest request) {
        UUID newId = UUID.randomUUID();
        LoggingRequest lr = new LoggingRequest(newId, request.msg);

        // Випадковий вибір лог-сервісу
        String chosen = chooseRandomInstance();
        String targetUrl = String.format("http://%s/api/log", chosen);

        log.info("[Facade] Відправляю POST -> http://{}/api/log : {{id={}, msg=\"{}\"}}",
                 chosen, lr.id, lr.msg);

        try {
            webClient.post()
                    .uri(URI.create(targetUrl))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(lr)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return ResponseEntity.ok(
                    String.format("Повідомлення з ID=%s успішно надіслано до %s", newId, chosen)
            );
        } catch (Exception ex) {
            log.error("[Facade] Помилка при POST до logging-service {}: {}", chosen, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Не вдалося записати повідомлення. Спробуйте пізніше.");
        }
    }

    /** 
     * GET /api/facade/logs 
     *  – Випадково обирає одну адресу logging-service і виконує GET /api/logs 
     *  – Повертає клієнтові JSON, який отримано від logging-service. 
     */
    @GetMapping("/logs")
    public ResponseEntity<Object> getAllLogs() {
        String chosen = chooseRandomInstance();
        String targetUrl = String.format("http://%s/api/logs", chosen);

        log.info("[Facade] Відправляю GET -> http://{}/api/logs", chosen);
        try {
            Object responseBody = webClient.get()
                    .uri(URI.create(targetUrl))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            return ResponseEntity.ok(responseBody);
        } catch (Exception ex) {
            log.error("[Facade] Помилка при GET з logging-service {}: {}", chosen, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Не вдалося отримати повідомлення. Спробуйте пізніше.");
        }
    }

    /** Допоміжний метод: обирає один елемент із списку loggingInstances випадково */
    private String chooseRandomInstance() {
        int idx = new Random().nextInt(loggingInstances.size());
        return loggingInstances.get(idx);
    }
}
