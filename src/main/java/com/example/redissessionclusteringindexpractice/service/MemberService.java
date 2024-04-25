package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.domain.dto.LoginDto;
import com.example.redissessionclusteringindexpractice.domain.dto.MemberRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.MemberResponse;
import com.example.redissessionclusteringindexpractice.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public List<Member>memberList(){
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MemberResponse memberResponse(Long id){
        Optional<Member>member = memberRepository.findById(id);
        return MemberResponse
                .builder()
                .member(member.get())
                .build();
    }

    //회원 가입
    @Transactional
    public Member memberCreate(MemberRequest memberRequest){
        memberRequest.setPassword(bCryptPasswordEncoder.encode(memberRequest.getPassword()));
        return memberRepository.save(memberRequest.toEntity(memberRequest));
    }

    @Transactional
    public Object login(HttpSession session, LoginDto loginDto){
        Member member = memberRepository.findByUserId(loginDto.getUserId());

        //회원객체가 없거나 비밀번호가 일치하지 않는 경우
        if(member==null || !bCryptPasswordEncoder.matches(loginDto.getPassword(),member.getPassword())){
            throw new RuntimeException("비밀이 맞지 않습니다.");
        }
        //아닌 경우에는 세션에 회원객체를 저장한다.
        session.setAttribute("member",member);
        log.info(session.getAttribute("member"));
        log.info(session.getId());
        return session.getId();
    }

    public void logout(HttpSession httpSession){
        httpSession.removeAttribute("member");
    }

}
