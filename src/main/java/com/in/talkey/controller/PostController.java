package com.in.talkey.controller;


import com.in.talkey.dto.CreatePostDto;
import com.in.talkey.dto.UpdatePostDto;
import com.in.talkey.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> Create(@ModelAttribute CreatePostDto createReq, Principal principal){
        return postService.Create(createReq, principal);
    }

    @GetMapping
    public ResponseEntity<?> Get(Principal principal){
        return postService.GetPosts(principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, Principal principal){
        return postService.Delete(id, principal);
    }

    @PatchMapping
    public ResponseEntity<?> Update(@ModelAttribute UpdatePostDto updateReq, Principal principal){
        return postService.Update(updateReq, principal);
    }

    @GetMapping("/show")
    public ResponseEntity<?> ShowPosts(Principal principal){
        return postService.ShowPost(principal);
    }
}
