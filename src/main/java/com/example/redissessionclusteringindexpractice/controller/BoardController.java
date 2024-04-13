package com.example.redissessionclusteringindexpractice.controller;

import com.example.redissessionclusteringindexpractice.config.aop.CheckedLogin;
import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public ResponseEntity<?>boardList(@RequestParam(value = "page") int offset,
                                      @RequestParam(value = "size") int limit){
        List<BoardResponse>boardList = boardService.list(offset,limit);
        return new ResponseEntity<>(boardList, HttpStatus.OK);
    }

    @GetMapping("/list-paging")
    public ResponseEntity<?>boardPaging(@PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<BoardResponse>list = boardService.boardLimit(pageable);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/off-list")
    public ResponseEntity<?>listOffSet(@RequestParam(defaultValue = "5") Long size,@RequestParam(value = "lastId") Long boardId){
        List<BoardResponse>list = boardService.nooffSetList(size,boardId);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>boardDetail(@PathVariable("id")Long id,HttpSession httpSession){
        Object currentUser = httpSession.getAttribute("member");
        if (currentUser!=null){
            BoardResponse boardResponse = boardService.boardDetail(id);
            log.info(boardResponse);
            return new ResponseEntity<>(boardResponse,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @CheckedLogin
    @PostMapping("/create")
    public ResponseEntity<?>boardCreate(@RequestBody BoardRequest boardRequest, Member member, HttpSession httpSession){
        //리팩토링 필요;
        Object currentUser = httpSession.getAttribute("member");
        if(currentUser!=null){
            member = ((Member)currentUser);
            Long createdResult = boardService.boardCreate(boardRequest,member);
            return new ResponseEntity<>(createdResult,HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @CheckedLogin
    @DeleteMapping("/{id}")
    public ResponseEntity<?>boardDelete(@PathVariable(value ="id")Long boardId, Member member,HttpSession session){
        Object currentUser = session.getAttribute("member");

        log.info("current-User::"+currentUser);

        if(currentUser!=null){
            member = ((Member)currentUser);
            log.info(member);
            boardService.boardDelete(boardId,member);
            return new ResponseEntity<>("Delete O.K",HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
