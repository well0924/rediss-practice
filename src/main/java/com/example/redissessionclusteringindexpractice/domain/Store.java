package com.example.redissessionclusteringindexpractice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Proxy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@ToString
@Proxy(lazy = false)
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeName;

    @OneToMany(mappedBy = "store",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Comment>list = new ArrayList<>();
}
