package com.in.talkey.controller;


import com.in.talkey.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    LikeController(LikeService likeService){
        this.likeService = likeService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> Like(@PathVariable Integer postId, Principal principal){
        return likeService.Like(postId, principal);
    }


}
