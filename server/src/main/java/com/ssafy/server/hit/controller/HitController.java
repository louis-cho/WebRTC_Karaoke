package com.ssafy.server.hit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.hit.service.HitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hit")
public class HitController {

    @Autowired
    private final HitService hitService;

    HitController(HitService hitService) {
        this.hitService = hitService;
    }

    public void createHit(@RequestBody JsonNode request) {
        int feedId = Integer.parseInt(request.get("feedId").asText());
        UUID uuid = UUID.fromString(request.get("uuid").asText());

        Hit hit = new Hit();
        hitService.createHit(hit);
    }
}
