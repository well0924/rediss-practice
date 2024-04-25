package com.example.redissessionclusteringindexpractice.domain.dto;

import com.example.redissessionclusteringindexpractice.domain.Board;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse implements Serializable {
    private Long id;
    private String title;
    private String author;
    private String contents;
    private Long readCount;
    private Long likedCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    @QueryProjection
    public BoardResponse(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.author = board.getMember().getUserId();
        this.contents = board.getContents();
        this.readCount = board.getReadCount();
        this.likedCount = board.getLikedCount();
        this.createdTime = board.getCreatedTime();
        this.updatedTime = board.getUpdatedTime();
    }

    public static BoardResponse toResponse(Board board){
        return BoardResponse
                .builder()
                .board(board)
                .build();
    }
}
