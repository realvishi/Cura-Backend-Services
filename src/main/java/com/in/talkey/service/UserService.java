package com.in.talkey.service;

import com.in.talkey.dto.LoginDto;
import com.in.talkey.dto.RegisterDto;
import com.in.talkey.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

     ResponseEntity<?> Register(RegisterDto registerReq);

     ResponseEntity<?> Login(LoginDto loginReq);

     ResponseEntity<?> Verify(String token);

     ResponseEntity<?> ForgotPassword(String email);

     ResponseEntity<?> ResetPassword(String newPassword, String token);
}
