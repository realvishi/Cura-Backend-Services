package com.in.talkey.service.implement;

import com.in.talkey.dto.CommentDto;
import com.in.talkey.dto.CreatePostDto;
import com.in.talkey.dto.PostDto;
import com.in.talkey.dto.UpdatePostDto;
import com.in.talkey.entity.Posts;
import com.in.talkey.entity.Users;
import com.in.talkey.repository.PostsRepository;
import com.in.talkey.repository.UsersRepository;
import com.in.talkey.service.CloudinaryService;
import com.in.talkey.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PostServiceImpl implements PostService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final CloudinaryService cloudinaryService;

    PostServiceImpl(PostsRepository postsRepository, UsersRepository usersRepository, CloudinaryService cloudinaryService){
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.cloudinaryService = cloudinaryService;
    }


    @Override
    public ResponseEntity<?> Create(CreatePostDto createReq, Principal principal) {
        try {

            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new UsernameNotFoundException("Token is not valid."));
            String[] cloudinaryRes = cloudinaryService.UploadImage(createReq.getImage(), principal.getName());

            Posts post = new Posts();
            post.setUser(user);
            post.setTitle(createReq.getTitle());
            post.setCaption(createReq.getCaption());
            post.setImageUrl(cloudinaryRes[0]);
            post.setImageId(cloudinaryRes[1]);

            postsRepository.save(post);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Post Created Successfully.");
        }
        catch (Exception e){
            System.out.println("ERROR: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please try later !!");
        }
    }

    @Override
    public ResponseEntity<?> GetPosts(Principal principal) {
        try {
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new UsernameNotFoundException("Token is not Valid"));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(postsRepository.findByUser(user));
        }
        catch (Exception e){
            System.out.println("ERROR: \n" + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal sever error");
        }

    }

    @Transactional
    @Override
    public ResponseEntity<?> Delete(Integer id, Principal principal) {
        try {
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("Token is not Valid"));
            Posts post = postsRepository.findById(id).orElseThrow(()-> new RuntimeException("Post Not found with id: "));
            if (post.getUser().equals(user)) {
                cloudinaryService.DeleteImage(post.getImageId());
                postsRepository.delete(post);
                return ResponseEntity.ok("Post Deleted Successfully with id: " + id);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Authorized to Delete post wit id:" + id);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }


    @Override
    public ResponseEntity<?> Update(UpdatePostDto updateReq, Principal principal) {
        try {
            if(updateReq.getId() == 0){
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("PostID is a required parameter");
            }
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new RuntimeException("Token is not Valid"));

            Posts post = postsRepository.findByIdAndUser(updateReq.getId(), user).orElseThrow(()-> new RuntimeException("Post Does not Exist, or not authorised for action."));

            if(updateReq.getImage() != null){
                String[] cloudinaryRes = cloudinaryService.UploadImage(updateReq.getImage(), principal.getName());
                post.setImageUrl(cloudinaryRes[0]);
                post.setImageId(cloudinaryRes[1]);
            }
            if(updateReq.getTitle() != null){
                post.setTitle(updateReq.getTitle());
            }
            if(updateReq.getCaption() != null){
                post.setCaption(updateReq.getCaption());
            }

            postsRepository.save(post);

            return ResponseEntity.ok("Post Updated Successfully");
        }
        catch (Exception e){
            System.out.println("ERROR: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal Server Error");
        }
    }

    @Override
    public ResponseEntity<?> ShowPost(Principal principal) {
        try {
            List<Posts> postsList = postsRepository.findTop20ByOrderByCreatedAtDesc();
            List<PostDto> postDtos = postsList.stream().map(post -> {
                PostDto postDto = new PostDto();
                postDto.setId(String.valueOf(post.getId()));
                postDto.setTitle(post.getTitle());
                postDto.setCaption(post.getCaption());
                postDto.setImageUrl(post.getImageUrl());
                postDto.setUpdatedAt(post.getUpdatedAt().toString());
                postDto.setUserName(post.getUser().getName());
                postDto.setUserImageUrl(post.getUser().getProfileImageUrl());
                postDto.setLikes(post.getLikesList().size());

                boolean isLiked = post.getLikesList().stream()
                        .anyMatch(like -> like.getUser().getUsername().equals(principal.getName()));
                postDto.setLiked(isLiked);

                List<CommentDto> commentDtos = post.getCommentsList().stream().map(comment -> {
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
                postDto.setCommentsList(commentDtos);
                return postDto;
            }).toList();

            return ResponseEntity.ok(postDtos);
        }
        catch (Exception e){
            System.out.println("ERROR: \n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal Server Error");
        }

    }


}
