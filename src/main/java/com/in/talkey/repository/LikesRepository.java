package com.in.talkey.repository;

import com.in.talkey.entity.Likes;
import com.in.talkey.entity.Posts;
import com.in.talkey.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Integer> {

     Optional<Likes> findByPostAndUser(Posts post, Users user);

     @Modifying
     @Query("DELETE FROM Likes l WHERE l = :entity")
     void delete(@Param("entity") Likes entity);
}
