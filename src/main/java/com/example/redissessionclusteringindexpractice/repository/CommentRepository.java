package com.example.redissessionclusteringindexpractice.repository;

import com.example.redissessionclusteringindexpractice.domain.Comment;
import com.example.redissessionclusteringindexpractice.domain.dto.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query(value = "select c from Comment c where c.store.id = :id")
    List<CommentResponse>commentList(@Param("id") Long storedId);
}
