package com.in.talkey.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Getter
public class UpdateCommentDto {

    private Integer commentId;
    private String content;
}
