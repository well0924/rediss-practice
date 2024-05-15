package com.example.redissessionclusteringindexpractice.domain.dto;

import com.example.redissessionclusteringindexpractice.domain.Comment;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String contents;
    private Double rating;

    @Builder
    public CommentResponse(Comment comment){
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.rating = comment.getRating();
    }
}
