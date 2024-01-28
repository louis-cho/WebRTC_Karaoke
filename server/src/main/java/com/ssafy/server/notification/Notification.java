package com.ssafy.server.notification;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/notifications")
public class Notification {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String userId) throws IOException {
        SseEmitter emitter = new SseEmitter();
        emitters.put(userId, emitter);

        emitter.onTimeout(() -> emitters.remove(userId, emitter));
        emitter.onCompletion(() -> emitters.remove(userId, emitter));

        return emitter;
    }

    @PostMapping("/sendNotification")
    public void sendNotification(@RequestParam String message, @RequestParam String userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message, MediaType.TEXT_PLAIN));
            } catch (IOException e) {
                emitters.remove(userId, emitter);
            }
        }
    }
}