package com.in.talkey.service.implement;

import com.cloudinary.Cloudinary;
import com.in.talkey.service.CloudinaryService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary){
        this.cloudinary = cloudinary;
    }

    @Override
    public String[] UploadImage(MultipartFile image, String location) {
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", location);
            System.out.println("***Start file upload****");
            Map uploadedFile = cloudinary.uploader().upload(image.getBytes(), options);
            System.out.println("*** File Uploaded Successfully *** \n" + uploadedFile);
            String publicId = (String) uploadedFile.get("public_id");
            String url = cloudinary.url().secure(true).generate(publicId);

            return new String[]{url, publicId};
        } catch (IOException e) {
            System.out.println("Error" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public Boolean DeleteImage(String imageId) {
        try {
            Map result = cloudinary.uploader().destroy(imageId, new HashMap<>());
            return result != null && result.get("result").equals("ok");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}
