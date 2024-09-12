package com.in.talkey.service.implement;

import com.in.talkey.entity.Likes;
import com.in.talkey.entity.Posts;
import com.in.talkey.entity.Users;
import com.in.talkey.repository.LikesRepository;
import com.in.talkey.repository.PostsRepository;
import com.in.talkey.repository.UsersRepository;
import com.in.talkey.service.JwtService;
import com.in.talkey.service.LikeService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Component
public class LikeServiceImpl implements LikeService {

    private final LikesRepository likesRepository;
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    LikeServiceImpl(LikesRepository likesRepository, JwtService jwtService, UsersRepository usersRepository, PostsRepository postsRepository){
        this.likesRepository = likesRepository;
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.postsRepository = postsRepository;
    }


    @Transactional
    @Override
    public ResponseEntity<?> Like(Integer postId, Principal principal) {
        try {
            Users user = usersRepository.findByEmail(principal.getName()).orElseThrow(()-> new UsernameNotFoundException("The Token is not Valid"));
            Posts post = postsRepository.findById(postId).orElseThrow(()-> new UnsupportedOperationException("The post with id: "+ postId + " is not available"));

            Optional<Likes> existLike = likesRepository.findByPostAndUser(post, user);
            if(existLike.isPresent()){
                likesRepository.delete(existLike.get());
                return ResponseEntity.ok("Dislike Successfully to Post :" + postId + " by "+ user.getName());
            }
            else{
                Likes like = new Likes();
                like.setUser(user);
                like.setPost(post);
                likesRepository.save(like);
                return ResponseEntity.ok("Like Successfully to Post :" + postId + " by "+ user.getName());
            }
        }
        catch (UsernameNotFoundException | UnsupportedOperationException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (Exception e){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
