package com.example.redissessionclusteringindexpractice.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@AttributeOverrides({
        @AttributeOverride(name = "id",column = @Column(name = "like_id"))
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Likes extends BaseEntity{

    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Likes(Member member, Board board,LocalDateTime createdTime, LocalDateTime updatedTime){
        this.member = member;
        this.board = board;
        this.getCreatedTime();
        this.getUpdatedTime();
    }
}
