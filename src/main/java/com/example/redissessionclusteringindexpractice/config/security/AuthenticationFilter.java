package com.example.redissessionclusteringindexpractice.config.security;

import com.example.redissessionclusteringindexpractice.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import static java.util.Objects.isNull;

@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {
    private final static String LOGIN_URL = "/api/login";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("authenticationFilter !!!!!!");
        log.info(request.getRequestURI());


        Member user = (Member) request.getSession().getAttribute("member");


        if(request.getRequestURI().equals(LOGIN_URL)&&Objects.isNull(user)){
            log.info("login !!");
            filterChain.doFilter(request, response);
            return;
        }

        if(!Objects.isNull(user)) {
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString()); // 사용자 권한
            log.info(authority);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(authority)); // 현재 사용자의 인증 정보
            log.info(authentication);
            log.info("filter member data:::"+user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }
}
