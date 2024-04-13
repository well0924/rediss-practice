package com.example.redissessionclusteringindexpractice.domain.dto;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {

    private String title;

    private String contents;

}
