package com.example.redissessionclusteringindexpractice.controller;

import com.example.redissessionclusteringindexpractice.service.LikesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LikesController {

    private final LikesService likesService;


}
