package com.in.talkey.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class ResetDto {
    private String newPassword;
    private String token;
}
