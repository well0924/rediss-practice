package com.example.redissessionclusteringindexpractice.domain.dto;

import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.domain.Role;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequest {

    private String userId;
    private String password;
    private String userPhone;
    private String userName;


    public  Member toEntity(MemberRequest request){
        return Member.builder()
                .userId(request.userId)
                .password(request.password)
                .userName(request.userName)
                .userPhone(request.userPhone)
                .role(Role.ROLE_ADMIN)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }
}
