package com.example.logging;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/log")
public class LoggingController {

    private final IMap<UUID, String> messagesMap;

    public LoggingController(HazelcastInstance instance) {
        this.messagesMap = instance.getMap("messages");
    }

    @PostMapping
    public void log(@RequestBody Message message) {
        messagesMap.put(message.getId(), message.getMsg());
    }

    @GetMapping
    public Collection<String> getAll() {
        return messagesMap.values();
    }

    // Внутрішній клас для передачі даних повідомлення
    public static class Message {
        private UUID id;
        private String msg;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
