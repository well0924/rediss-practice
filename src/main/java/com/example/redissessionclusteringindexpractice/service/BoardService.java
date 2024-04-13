package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.config.redis.CacheKey;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import com.example.redissessionclusteringindexpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final ReadCountService readCountService;

    //size = limit
    //page = offset
    @Transactional(readOnly = true)
    public List<BoardResponse>list(final int offset,final int limit){
        return boardRepository.findAll(PageRequest.of(offset,limit));
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse>boardLimit(Pageable pageable){
        return boardRepository.boardPaging(pageable);
    }

    @Transactional
    public List<BoardResponse>nooffSetList(Long size, Long boardId){
        return boardRepository.boardResponseList(size,boardId);
    }

    @Transactional
    @Cacheable(value = CacheKey.BOARD,key = "#boardId",unless = "#result == null")
    public BoardResponse boardDetail(Long boardId){

        log.info("service!");
        BoardResponse detail = boardRepository.boardDetail(boardId);
        String key = CacheKey.BOARD +":"+ boardId;
        //조회수 증가.
        //readCountService.readCountUp(key,boardId);
        return detail;
    }

    @Transactional
    public Long boardCreate(BoardRequest boardRequest,Member member){

        Member memberDetail = memberRepository.findByUserId(member.getUserId());

        Board board = Board
                .builder()
                .author(member.getUserId())
                .title(boardRequest.getTitle())
                .contents(boardRequest.getContents())
                .readCount(0L)
                .member(memberDetail)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        return boardRepository.save(board).getId();
    }

    @Transactional
    public Long boardUpdate(Long boardId,BoardRequest boardRequest){
        return null;
    }


    @Transactional
    @CacheEvict(value = CacheKey.BOARD,key = "#boardId")
    public void boardDelete(Long boardId,Member member){
        Optional<Board>board = boardRepository.findById(boardId);
        Optional<Member>memberDetail = memberRepository.findById(member.getId());
        if(board.isEmpty()){
            throw new RuntimeException("게시글이 없습니다.");
        }
        boardRepository.deleteById(boardId);
    }

}
