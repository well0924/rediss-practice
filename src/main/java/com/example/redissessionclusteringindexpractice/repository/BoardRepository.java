package com.example.redissessionclusteringindexpractice.repository;

import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.repository.queryDsl.CustomBoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long>, CustomBoardRepository {

    @Query(value = "select b from Board b order by b.id desc")
    List<BoardResponse>findAll(PageRequest p);


}
