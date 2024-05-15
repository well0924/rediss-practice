package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.domain.Store;
import com.example.redissessionclusteringindexpractice.domain.dto.StoreResponse;
import com.example.redissessionclusteringindexpractice.repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public List<StoreResponse>storeList(){
        return storeRepository.findAll()
                .stream()
                .map(StoreResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreResponse storeDetail(Long storeId){
        Optional<Store>store = storeRepository.findById(storeId);
        return StoreResponse.builder()
                .store(store.get())
                .build();
    }

}
