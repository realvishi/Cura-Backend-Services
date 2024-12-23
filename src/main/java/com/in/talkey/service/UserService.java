package com.in.talkey.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.in.talkey.dto.LoginDto;
import com.in.talkey.dto.RegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

     ResponseEntity<?> Register(RegisterDto registerReq);

     ResponseEntity<?> Login(LoginDto loginReq);

     ResponseEntity<?> Verify(String token);

     ResponseEntity<?> ForgotPassword(String email);

     ResponseEntity<?> ResetPassword(String newPassword, String token);

     ResponseEntity<JsonNode> getRemedyJSON(String email);

     ResponseEntity<JsonNode> setRemedyJSON(String email, JsonNode remedy);
}
