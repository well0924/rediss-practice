package com.example.redissessionclusteringindexpractice.repository;

import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.repository.queryDsl.CustomBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long>, CustomBoardRepository {

    @Query(value = "select b from Board b order by b.id desc")
    List<BoardResponse>findAll(PageRequest p);

    @Query(value = "select b from Board b where b.id = :id")
    Board getBoardById(@Param("id") Long boardId);

    @Modifying
    @Transactional
    @Query(value = "update Board b set b.likedCount = +1 where b.id = :id")
    void likeUp(@Param("id")Long id);

    @Modifying
    @Transactional
    @Query(value = "update Board b set b.likedCount = -1 where b.id = :id")
    void likeDown(@Param("id")Long boarId);
}
