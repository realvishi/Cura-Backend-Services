package com.in.talkey.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {

    private Integer u_id;
    private String name;
    private String email;
    private String image_url;
    private String token;

}
