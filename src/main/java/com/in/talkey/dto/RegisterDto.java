package com.in.talkey.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RegisterDto {

    private String name;
    private String email;
    private String password;
    private MultipartFile profileImage;
}
