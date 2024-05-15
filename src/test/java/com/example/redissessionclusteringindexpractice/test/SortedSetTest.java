package com.example.redissessionclusteringindexpractice.test;

import com.example.redissessionclusteringindexpractice.domain.Store;
import com.example.redissessionclusteringindexpractice.domain.dto.CommentRequest;
import com.example.redissessionclusteringindexpractice.repository.CommentRepository;
import com.example.redissessionclusteringindexpractice.repository.StoreRepository;
import com.example.redissessionclusteringindexpractice.service.CommentService;
import com.example.redissessionclusteringindexpractice.service.RatingService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SortedSetTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /*@BeforeEach
    public void setUp(){
        // 데이터 초기화
      redisTemplate.getConnectionFactory().getConnection().flushDb();
      commentRepository.deleteAll();
      Store store1 = Store.builder()
                .placeName("Store 1")
                .build();
        storeRepository.save(store1);

        Store store2 = Store.builder()
                .placeName("Store 2")
                .build();

        storeRepository.save(store2);

        Store store3 = Store.builder()
                .placeName("Store 3")
                .build();
        storeRepository.save(store3);
    }
*/

    @Test
    @Disabled
    @DisplayName("댓글을 저장후 reids에 평점이 들어가는지를 확인 + 평점이 나오는지를 확인하기.")
    public void saveCommentTest(){
        //5
        commentService.saveComment(7L,commentRequest1());
        //4
        commentService.saveComment(7L,commentRequest2());
        //3
        commentService.saveComment(8L,commentRequest3());

        Double avgRating1 = redisTemplate.opsForZSet().score("storeRatings", "7");
        Double avgRating2 = redisTemplate.opsForZSet().score("storeRatings", "8");

        System.out.println("4번가게 평점::"+avgRating1);

        System.out.println("6번가게 평점::"+avgRating2);

        assertNotNull(avgRating1);
        Assertions.assertEquals(4.5, avgRating1,0.001);
        assertNotNull(avgRating2);
        Assertions.assertEquals(3.0, avgRating2, 0.001);
    }

    @Test
    @DisplayName("평점이 높은 가게 top5 출력하기.")
    public void testTotalCommentRatingStore(){
        // 댓글 추가 및 평점 저장
        commentService.saveComment(10L, commentRequest1());
        commentService.saveComment(10L, commentRequest2());
        commentService.saveComment(11L, commentRequest3());
        commentService.saveComment(12L, commentRequest1());
        commentService.saveComment(12L, commentRequest2());
        commentService.saveComment(12L, commentRequest1());

        List<Store> topStores = ratingService.getTopRatedStores();
        System.out.println(topStores.stream().collect(Collectors.toList()));
        Assertions.assertEquals(3, topStores.size());
        //Assertions.assertEquals("Store 3", topStores.get(0).getPlaceName());
        //Assertions.assertEquals("Store 1", topStores.get(1).getPlaceName());
        //Assertions.assertEquals("Store 2", topStores.get(2).getPlaceName());

    }

    private CommentRequest commentRequest1(){
        return CommentRequest.builder()
                .contents("comment1")
                .rating(5.0)
                .build();
    }

    private CommentRequest commentRequest2(){
        return CommentRequest
                .builder()
                .contents("comment2")
                .rating(4.0)
                .build();
    }

    private CommentRequest commentRequest3(){
        return CommentRequest
                .builder()
                .contents("comment3")
                .rating(3.0)
                .build();
    }
}
