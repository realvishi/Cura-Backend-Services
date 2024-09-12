package com.in.talkey.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {
    private Integer postId;
    private String content;
}
