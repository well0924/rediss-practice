package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.config.redis.CacheKey;
import com.example.redissessionclusteringindexpractice.config.redis.DistributeLock;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
@RequiredArgsConstructor
public class ReadCountService {

    private final BoardRepository boardRepository;

    @DistributeLock(key = "#lockKey")
    public BoardResponse boardDetailReadCountUp(String lockKey,Long boardId){
        Board board = boardRepository.getBoardById(boardId);
        board.countUp();
        return BoardResponse.toResponse(board);
    }
}
