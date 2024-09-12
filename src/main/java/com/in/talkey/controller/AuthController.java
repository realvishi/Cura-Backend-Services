package com.in.talkey.controller;

import com.in.talkey.dto.LoginDto;
import com.in.talkey.dto.RegisterDto;
import com.in.talkey.dto.ResetDto;
import com.in.talkey.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;


    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute RegisterDto registerReq){
        if (registerReq.getProfileImage() == null || registerReq.getProfileImage().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is missing");
        }
        return userService.Register(registerReq);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginReq) {
        return userService.Login(loginReq);
    }


    @GetMapping("/verify")
    public ResponseEntity<?> VerifyToken(@RequestParam("token")String token){
        return userService.Verify(token);
    }


    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email")String email){
        return userService.ForgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetDto resetReq){
        return userService.ResetPassword(resetReq.getNewPassword(), resetReq.getToken());
    }


    @RequestMapping("/allowed")
    public String allowed(){
        return "This endpoint is open.";
    }
}
