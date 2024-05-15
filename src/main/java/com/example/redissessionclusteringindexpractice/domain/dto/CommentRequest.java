package com.example.redissessionclusteringindexpractice.domain.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String contents;
    private Double rating;

}
