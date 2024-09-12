package com.in.talkey.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface LikeService {

    ResponseEntity<?> Like(Integer postId, Principal principal);

}
