package com.ssafy.server.notification.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
public class SseEmitters {

    private static final AtomicLong counter = new AtomicLong();

    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>(); //thread-safe한 자료구조.

    public SseEmitter add(Integer userPk, SseEmitter emitter) {
        this.emitters.put(userPk, emitter);
        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitters.size());
        log.info("emitter list: {}", emitters);
        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            this.emitters.remove(userPk, emitter);
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
        });

        return emitter;
    }

    public SseEmitter getSseEmitter(Integer userPk){
        return emitters.get(userPk);
    }
    public void remove(Integer userPk, SseEmitter emitter){
        emitters.remove(userPk, emitter);
    }

}
