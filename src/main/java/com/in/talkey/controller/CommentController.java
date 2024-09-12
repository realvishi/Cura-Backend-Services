package com.in.talkey.controller;


import com.in.talkey.dto.CreateCommentDto;
import com.in.talkey.dto.UpdateCommentDto;
import com.in.talkey.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{postId}")
    public  ResponseEntity<?> Get(@PathVariable Integer postId, Principal principal){
        return commentService.Get(postId, principal);
    }


    @PostMapping
    public ResponseEntity<?> Add(@RequestBody CreateCommentDto createReq, Principal principal){
        return commentService.Add(createReq, principal);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> Delete(@PathVariable String id, Principal principal){
        return commentService.Delete(Integer.parseInt(id), principal);
    }

    @PatchMapping
    public ResponseEntity<?> Update(@RequestBody UpdateCommentDto updateReq, Principal principal){
        return commentService.Update(updateReq, principal);
    }

}
