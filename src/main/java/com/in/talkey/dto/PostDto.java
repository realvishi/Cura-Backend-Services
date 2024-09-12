package com.in.talkey.dto;


import com.in.talkey.entity.Comments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private String imageUrl;
    private String title;
    private String caption;
    private String updatedAt;
    private String userName;
    private String userImageUrl;
    private String id;
    private Integer likes;
    private Boolean liked;
    private List<CommentDto> commentsList;
}
