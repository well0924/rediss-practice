package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.config.redis.DistributeLock;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadCountService {

    private final BoardRepository boardRepository;

    @DistributeLock(key = "#lockKey")
    public void readCountUp(String lockKey,Long boardId){
        BoardResponse response = boardRepository.boardDetail(boardId);
        boardRepository.readCountUp(boardId);
    }
}
