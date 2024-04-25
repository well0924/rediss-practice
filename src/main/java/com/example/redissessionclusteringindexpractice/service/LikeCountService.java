package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.config.redis.DistributeLock;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.Likes;
import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import com.example.redissessionclusteringindexpractice.repository.LikesRepository;
import com.example.redissessionclusteringindexpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Log4j2
@Component
@AllArgsConstructor
public class LikeCountService {

    private final LikesRepository likesRepository;

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    @DistributeLock(key = "#key")
    public void likePlus(String key,Long memberId,Long boardId){
        Board board = boardRepository.getBoardById(boardId);
        Member member = memberRepository.findById(memberId).get();
        board.likeUp();
        log.info("likeCount:::"+board.getLikedCount());
        log.info("likes:::"+board.getLikes().size());
        Likes likes = Likes
                .builder()
                .board(board)
                .member(member)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        log.info("????:::"+likes.toString());
        likesRepository.save(likes);
    }

    @DistributeLock(key = "#key")
    public void likeMinus(String key,Member member,Board board){
        Likes likes = Likes
                .builder()
                .member(member)
                .board(board)
                .build();
        board.likeDown();
        likesRepository.delete(likes);
    }
}
