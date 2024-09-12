package com.in.talkey.service.implement;

import com.in.talkey.dto.CommentDto;
import com.in.talkey.dto.CreateCommentDto;
import com.in.talkey.dto.UpdateCommentDto;
import com.in.talkey.entity.Comments;
import com.in.talkey.entity.Posts;
import com.in.talkey.entity.Users;
import com.in.talkey.repository.CommentsRepository;
import com.in.talkey.repository.PostsRepository;
import com.in.talkey.repository.UsersRepository;
import com.in.talkey.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Component
public class CommentServiceImpl implements CommentService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final CommentsRepository commentsRepository;

    public CommentServiceImpl(PostsRepository postsRepository, UsersRepository usersRepository, CommentsRepository commentsRepository) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.commentsRepository = commentsRepository;
    }

    @Override
    public ResponseEntity<?> Add(CreateCommentDto createReq, Principal principal) {
        try {
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new IllegalArgumentException("The user does not exist"));
            Posts post = postsRepository.findById(createReq.getPostId()).orElseThrow(()-> new IllegalArgumentException("The post does not exist"));

            Comments comment = new Comments();
            comment.setContent(createReq.getContent());
            comment.setPost(post);
            comment.setUser(user);

            commentsRepository.save(comment);
            return ResponseEntity.ok("Commented Successfully");
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Transactional
    @Override
    public ResponseEntity<?> Delete(Integer CommentId, Principal principal) {
        try {
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new IllegalArgumentException("The user does not exist"));
            Comments comment = commentsRepository.findById(CommentId).orElseThrow(()-> new IllegalArgumentException("The comment does not exist"));
            if(comment.getUser().equals(user)){
                commentsRepository.delete(comment);
                return ResponseEntity.ok("Comment deleted Successfully");
            }
            else{
                throw new AuthorizationServiceException("User is not authorized to delete the comment");
            }
        }
        catch ( IllegalArgumentException | AuthorizationServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Override
    public ResponseEntity<?>
    Update(UpdateCommentDto updateReq, Principal principal) {
        try {
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new IllegalArgumentException("The user does not exist"));
            Comments comment = commentsRepository.findById(updateReq.getCommentId()).orElseThrow(()-> new IllegalArgumentException("The comment does not exist"));
            if(comment.getUser().equals(user)){
                comment.setContent(updateReq.getContent());
                commentsRepository.save(comment);
                return ResponseEntity.ok("Comment updated Successfully");

            }
            else{
                throw new AuthorizationServiceException("User is not authorized to delete the comment");
            }
        }
        catch ( IllegalArgumentException | AuthorizationServiceException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Override
    public ResponseEntity<?> Get(Integer postId, Principal principal) {
        try {
            List<Comments> commentList = commentsRepository.findAllByPostId(postId);

            List<CommentDto> commentDtos = commentList.stream().map(comment -> {
                CommentDto commentDto = new CommentDto();
                commentDto.setContent(comment.getContent());
                commentDto.setUserName(comment.getUser().getName());
                commentDto.setUserImageUrl(comment.getUser().getProfileImageUrl()); // Assuming User has profileImageUrl
                commentDto.setCreatedAt(comment.getCreated_at());
                commentDto.setUpdatedAt(comment.getUpdated_at());
                commentDto.setUserId(comment.getUser_id());
                commentDto.setId(comment.getId());
                return commentDto;
            }).toList();

            return ResponseEntity.ok(commentDtos);
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


}







































