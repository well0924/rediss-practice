package com.example.redissessionclusteringindexpractice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "id",column = @Column(name = "member_id"))
})
public class Member extends BaseEntity implements Serializable {

    private Long id;
    private String userId;
    private String password;
    private String userName;
    private String userPhone;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(Long id, String userId, String password, String userName, String userPhone,
                  Role role, LocalDateTime createdTime,LocalDateTime updatedTime){
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.userPhone = userPhone;
        this.role = role;
        this.getUpdatedTime();
        this.getCreatedTime();
    }

}
