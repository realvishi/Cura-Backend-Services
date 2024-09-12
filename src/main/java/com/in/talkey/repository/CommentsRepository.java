package com.in.talkey.repository;

import com.in.talkey.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {

    @Modifying
    @Query("DELETE FROM Comments c WHERE c.id = :id")
    void deleteById(@Param("id") Integer integer);

    @Modifying
    @Query("DELETE FROM Comments c WHERE c = :entity")
    void delete(@Param("entity") Comments entity);

    List<Comments> findAllByPostId(Integer postId);
}
