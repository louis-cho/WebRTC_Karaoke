package com.ssafy.server.like.controller;

import com.ssafy.server.like.model.Likes;
import com.ssafy.server.like.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/create")
    public ResponseEntity<Likes> createLike(@RequestBody Likes newLike) {
        likeService.createLike(newLike);
        return new ResponseEntity<>(newLike, HttpStatus.CREATED);
    }

    @GetMapping("/{likeId}")
    public ResponseEntity<Likes> getLikeById(@PathVariable int likeId) {
        Likes like = likeService.getLikeById(likeId);

        if (like != null) {
            return new ResponseEntity<>(like, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{likeId}")
    public ResponseEntity<Likes> updateLike(@RequestBody Likes updatedLike) {
        Likes updated = likeService.updateLike(updatedLike);

        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{likeId}")
    public ResponseEntity<Boolean> deleteLike(@PathVariable int likeId) {
        boolean result = likeService.deleteLike(likeId);

        if (result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
