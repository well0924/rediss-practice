package com.example.redissessionclusteringindexpractice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor
public class Member implements Serializable {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String password;
    private String userName;
    private String userPhone;
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    public Member(Long id,String userId,String password,String userName,String userPhone,
                  Role role,
                  LocalDateTime createdTime,LocalDateTime updatedTime){
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.userPhone = userPhone;
        this.role = role;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

}
