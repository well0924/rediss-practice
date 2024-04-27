package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.config.redis.CacheKey;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import com.example.redissessionclusteringindexpractice.repository.LikesRepository;
import com.example.redissessionclusteringindexpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@AllArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    private final LikeCountService likeCountService;

    //좋아요 중복처리.
    @Transactional
    public boolean duplicatedLikes(Long boardId,Long memberId){
        return likesRepository.findByBoardAndMember(boardId,memberId);
    }

    //좋아요 증가
    public void likPlus(Long boardId, Long memberId){
        log.info("service");
        String key = CacheKey.LIKES+":"+boardId;
        log.info("key??:::"+key);
        likeCountService.likePlus(key,memberId,boardId);
    }

    //좋아요 감소
    public void likeMinus(Board board ,Member member){
        boolean result = duplicatedLikes(board.getId(),member.getId());

        String key = CacheKey.LIKES + ":" + member.getId();

        if(result ==true){
            likeCountService.likeMinus(key,member,board);
        }
    }
}
