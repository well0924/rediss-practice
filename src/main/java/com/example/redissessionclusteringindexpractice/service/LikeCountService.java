package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.config.redis.DistributeLock;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.Likes;
import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import com.example.redissessionclusteringindexpractice.repository.LikesRepository;
import com.example.redissessionclusteringindexpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class LikeCountService {

    private final LikesRepository likesRepository;

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    @DistributeLock(key = "#key")
    public void likePlus(String key,Long memberId,Long boardId){
        Optional<Board> board = boardRepository.findById(boardId);
        Optional<Member> member = memberRepository.findById(memberId);
        board.get().likeUp();
        log.info("likeCount:::"+board.get().getLikedCount());
        log.info("likes:::"+board.get().getLikes().size());
        Likes likes = new Likes(member.get(),board.get());
        log.info("????:::"+likes);
        //boardRepository.likeUp(boardId);
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
