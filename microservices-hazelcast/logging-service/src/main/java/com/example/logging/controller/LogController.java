package com.example.logging.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LogController {

    private final Logger log = LoggerFactory.getLogger(LogController.class);

    private final IMap<UUID, String> messagesMap;

    @Autowired
    public LogController(HazelcastInstance hazelcastInstance) {
        // Spring автоматично створить HazelcastInstanceBean на основі вашого @Bean Config
        this.messagesMap = hazelcastInstance.getMap("messages");
    }

    public static class LogEntry {
        @JsonProperty("id")
        public UUID id;
        @JsonProperty("msg")
        public String msg;
    }

    @PostMapping("/log")
    public void postLog(@RequestBody LogEntry entry) {
        messagesMap.put(entry.id, entry.msg);
        log.info("[LoggingService@{}] Збережено: {} → \"{}\"",
                 // отримати порт із середовища
                 System.getProperty("server.port"), entry.id, entry.msg);
    }

    @GetMapping("/logs")
    public Map<UUID, String> getAllLogs() {
        log.info("[LoggingService@{}] GET /api/logs → повертаємо {} записів",
                 System.getProperty("server.port"), messagesMap.size());
        return messagesMap;
    }
}
