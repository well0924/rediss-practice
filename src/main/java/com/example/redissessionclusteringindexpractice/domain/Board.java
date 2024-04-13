package com.example.redissessionclusteringindexpractice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "board",indexes = {
        @Index(name = "index_board_title",columnList = "title",unique = true),
        @Index(name = "index_board_author",columnList = "author")})
@Getter
@ToString
@NoArgsConstructor
public class Board {

    @Id
    @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;
    private String author;
    private Long readCount;

    @ToString.Exclude
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    public Board(Long id,String title,String author,String contents,Long readCount,Member member,LocalDateTime createdTime,LocalDateTime updatedTime){
        this.id = id;
        this.author = member.getUserId();
        this.title = title;
        this.contents = contents;
        this.readCount = readCount;
        this.member = member;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }


    //조회수 증가
    public void countUp(){
        this.readCount++;
    }
}
