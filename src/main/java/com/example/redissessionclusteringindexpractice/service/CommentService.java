package com.example.redissessionclusteringindexpractice.service;

import com.example.redissessionclusteringindexpractice.domain.Comment;
import com.example.redissessionclusteringindexpractice.domain.Store;
import com.example.redissessionclusteringindexpractice.domain.dto.CommentRequest;
import com.example.redissessionclusteringindexpractice.domain.dto.CommentResponse;
import com.example.redissessionclusteringindexpractice.repository.CommentRepository;
import com.example.redissessionclusteringindexpractice.repository.StoreRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final StoreRepository storeRepository;

    private final RatingService ratingService;

    @Transactional
    public List<CommentResponse>commentResponseList(){
        return commentRepository.findAll()
                .stream().map(CommentResponse::new)
                .collect(Collectors.toList());
    }
    
    //댓글 작성
    @Transactional
    public void saveComment(Long storedId, CommentRequest commentRequest){
        Optional<Store>store = storeRepository.findById(storedId);
        log.info(store.get());
        if(store.isPresent()){
            Comment comment = Comment
                    .builder()
                    .store(store.get())
                    .rating(commentRequest.getRating())
                    .contents(commentRequest.getContents())
                    .build();
            commentRepository.save(comment);
            //평점계산
            double avgRating = calculateAverageComment(storedId);
            System.out.println("Calculated average rating for store " + storedId + " is " + avgRating);
            ratingService.saveRating(storedId.toString(),avgRating);
        }
    }

    //평점 계산
    private Double calculateAverageComment(Long storeId){

        List<CommentResponse>commentResponseList = commentRepository.commentList(storeId);

        if(commentResponseList!=null && !commentResponseList.isEmpty()){
            double totalRating = 0.0;
            for (CommentResponse commentResponse : commentResponseList) {
                totalRating += commentResponse.getRating();
            }
            return totalRating/commentResponseList.size();
        }
        return 0.0;
    }
}
