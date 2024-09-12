package com.in.talkey.repository;

import com.in.talkey.entity.Posts;
import com.in.talkey.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {

    List<Posts> findByUser(Users user);

    Optional<Posts> findByIdAndUser(Integer id, Users user);

    @Modifying
    @Query("DELETE FROM Posts p WHERE p.id = :id")
    void deleteById(@Param("id") Integer integer);

    @Modifying
    @Query("DELETE FROM Posts p WHERE p = :entity")
    void delete(@Param("entity") Posts entity);

    List<Posts> findTop20ByOrderByCreatedAtDesc();

}
