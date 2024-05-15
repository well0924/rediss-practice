package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.domain.Store;
import com.example.redissessionclusteringindexpractice.repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RatingService {

    private final RedisTemplate<String,String>redisTemplate;

    private final StoreRepository storeRepository;

    //평점 저장
    public void saveRating(String storeId, double rating) {
        redisTemplate.opsForZSet().add("storeRatings", storeId, rating);
        System.out.println("Saved rating for store " + storeId + " with rating " + rating);
    }

    //평점 top5
    public List<Store> getTopRatedStores() {

        Set<String> topStores = redisTemplate.opsForZSet().reverseRange("storeRatings", 0, 4);

        List<Store>storeList = new ArrayList<>();

        for (String storeId : topStores) {
            storeRepository.findById(Long.parseLong(storeId))
                    .ifPresent(store -> storeList.add(store));
        }

        return storeList;
    }
}
