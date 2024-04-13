package com.example.redissessionclusteringindexpractice.domain.dto;

import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.domain.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String userId;
    private String password;
    private String userName;
    private String userPhone;
    private Role role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    @QueryProjection
    public MemberResponse(Member member){
        this.memberId = member.getId();
        this.userId = member.getUserId();
        this.password = member.getPassword();
        this.userName = member.getUserName();
        this.userPhone = member.getUserPhone();
        this.role = member.getRole();
        this.createdTime = member.getCreatedTime();
        this.updatedTime = member.getUpdatedTime();
    }
}
