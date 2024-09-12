package com.in.talkey.controller;


import com.in.talkey.dto.RegisterDto;
import com.in.talkey.entity.Users;
import com.in.talkey.helper.Response;
import com.in.talkey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/home")
    public String home(){
        return "Welcome to the Home Page!!";
    }


}
