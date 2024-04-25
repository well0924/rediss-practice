package com.example.redissessionclusteringindexpractice.controller;

import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.domain.dto.LoginDto;
import com.example.redissessionclusteringindexpractice.domain.dto.MemberRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.MemberResponse;
import com.example.redissessionclusteringindexpractice.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<?>memberList(){
        List<MemberResponse>memberResponses = memberService.memberList()
                .stream()
                .map(member -> new MemberResponse(member))
                .toList();
        return new ResponseEntity<>(memberResponses,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>memberDetail(@PathVariable("id")Long memberId){
        MemberResponse memberResponses = memberService.memberResponse(memberId);
        return new ResponseEntity<>(memberResponses,HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?>memberCreate(@RequestBody MemberRequest memberRequest){
        Member member = memberService.memberCreate(memberRequest);
        return new ResponseEntity<>(member,HttpStatus.CREATED);
    }


    //loginResult 는 sessionId
    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginDto loginDto, HttpSession session){
        return new ResponseEntity<>(memberService.login(session,loginDto), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?>logout(HttpSession session){
        memberService.logout(session);
        session.invalidate();
        return new ResponseEntity<>("logout",HttpStatus.OK);
    }

    @GetMapping("/check-user")
    public ResponseEntity<?>memberCheck(HttpSession httpSession){
        String result;
        Member member = new Member();
        Object currentUser = httpSession.getAttribute("member");
        String id = httpSession.getId();
        log.info(currentUser);
        if(isNull(currentUser)){
            result = "로그인이 되어 있지 않음";
        }else{
            result = ((Member)currentUser).getUserId();
            member = ((Member)currentUser);
            log.info(member);
            log.info(id);
            log.info(httpSession.getAttribute("member"));
        }
        return new ResponseEntity<>(member,HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String>sessionTest(HttpSession session){
        session.setAttribute("member","sharing??");
        String sessionId = session.getId();
        return new ResponseEntity<>(sessionId,HttpStatus.OK);
    }
}
