package com.ssafy.server.notification;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
public class SseEmitters {

    private static final AtomicLong counter = new AtomicLong();

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>(); //thread-safe한 자료구조.

    SseEmitter add(String userId, SseEmitter emitter) {
        this.emitters.put(userId, emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        log.info("emitter list: {}", emitters);
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(userId, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    SseEmitter getSseEmitter(String userId){
        return emitters.get(userId);
    }
    void remove(String userId, SseEmitter emitter){
        emitters.remove(userId,emitter);
    }

}
