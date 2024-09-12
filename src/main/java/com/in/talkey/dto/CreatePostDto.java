package com.in.talkey.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Getter
public class CreatePostDto {

    private MultipartFile image;
    private String title;
    private String caption;

}
