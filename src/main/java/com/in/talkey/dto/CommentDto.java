package com.in.talkey.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Integer id;
    private String content;
    private String userName;
    private String userImageUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer userId;

}
