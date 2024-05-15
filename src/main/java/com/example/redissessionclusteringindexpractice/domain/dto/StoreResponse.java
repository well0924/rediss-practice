package com.example.redissessionclusteringindexpractice.domain.dto;

import com.example.redissessionclusteringindexpractice.domain.Store;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {

    private Long id;

    private String placeName;

    private List<CommentResponse>commentResponseList;

    @Builder
    public StoreResponse(Store store){
        this.id = store.getId();
        this.placeName = store.getPlaceName();
        this.commentResponseList = store.getList()!= null ?
                store.getList().stream().map(CommentResponse::new).collect(Collectors.toList()) : new ArrayList<>();
    }
}
