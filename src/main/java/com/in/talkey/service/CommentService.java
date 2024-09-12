package com.in.talkey.service;

import com.in.talkey.dto.CreateCommentDto;
import com.in.talkey.dto.UpdateCommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface CommentService {

    ResponseEntity<?> Add(CreateCommentDto createReq, Principal principal);
    ResponseEntity<?> Delete(Integer CommentId , Principal principal);
    ResponseEntity<?> Update(UpdateCommentDto updateReq, Principal principal);
    ResponseEntity<?> Get(Integer postId, Principal principal);
}
