package com.in.talkey.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String[] UploadImage(MultipartFile image, String location);
    Boolean DeleteImage(String url);
}
