package com.example.redissessionclusteringindexpractice.config.aop;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;

@Log4j2
@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    private final HttpSession httpSession;

    @Pointcut("@annotation(CheckedLogin)")
    public void checkedLoginPointcut() {}

    @Before("checkedLoginPointcut()")
    public void checkLogin(JoinPoint joinPoint) throws Exception {
        Object currentUser = httpSession.getAttribute("member");
        log.info(currentUser);

        if (currentUser == null) {
            throw new AuthenticationException("로그인이 필요합니다.");
        }
    }
}
