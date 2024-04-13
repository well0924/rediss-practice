package com.example.redissessionclusteringindexpractice.domain.dto;

import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String userId;
    private String password;
}
