package com.in.talkey.service;

import com.in.talkey.dto.CreatePostDto;
import com.in.talkey.dto.UpdatePostDto;
import com.in.talkey.entity.Posts;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Array;

@Service
public interface PostService {

    ResponseEntity<?> Create(CreatePostDto createReq , Principal principal);
    ResponseEntity<?> GetPosts(Principal principal);
    ResponseEntity<?> Delete(Integer id, Principal principal);
    ResponseEntity<?> Update(UpdatePostDto updateReq, Principal principal);
    ResponseEntity<?> ShowPost(Principal principal);
}
