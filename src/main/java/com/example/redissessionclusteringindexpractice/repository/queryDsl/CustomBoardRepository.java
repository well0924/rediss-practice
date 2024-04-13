package com.example.redissessionclusteringindexpractice.repository.queryDsl;

import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomBoardRepository {
    //게시글 목록 (페이징)
    Page<BoardResponse>boardPaging(Pageable pageable);
    //게시글 목록 (no-offset) -> 무한 스크롤로 구현하기.
    List<BoardResponse> boardResponseList(Long size, Long lastBoardId);
    //단일 조회
    BoardResponse boardDetail(Long boardId);
    //조회수 증가+1
    void readCountUp(Long boardId);
}
