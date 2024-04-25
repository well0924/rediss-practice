package com.example.redissessionclusteringindexpractice.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "board",indexes = {
        @Index(name = "index_board_title",columnList = "title",unique = true),
        @Index(name = "index_board_author",columnList = "author")})
@Getter
@ToString
@NoArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "id",column = @Column(name = "board_id"))
})
public class Board extends BaseEntity{

    private Long id;

    private String title;

    private String contents;

    private String author;

    @ColumnDefault("0")
    private Long readCount;

    @ColumnDefault("0")
    private Long likedCount;

    @ToString.Exclude
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ToString.Exclude
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private final Set<Likes> likes = new LinkedHashSet<>();

    @Builder
    public Board(Long id,String title,String author,String contents,Long readCount,Long likedCount,Member member,LocalDateTime createdTime,LocalDateTime updatedTime){
        this.id = id;
        this.author = member.getUserId();
        this.title = title;
        this.contents = contents;
        this.readCount = readCount;
        this.likedCount = likedCount;
        this.member = member;
        this.getCreatedTime();
        this.getUpdatedTime();
    }

    //조회수 증가
    public void countUp(){
        this.readCount+=1;
    }

    //좋아요 증가
    public void likeUp(){
        this.likedCount +=1;
    }
    //좋아요 감소
    public void likeDown(){
        this.likedCount -=1;
    }
}
