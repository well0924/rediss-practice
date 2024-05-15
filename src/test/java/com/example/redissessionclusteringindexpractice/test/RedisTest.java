package com.example.redissessionclusteringindexpractice.test;

import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.example.redissessionclusteringindexpractice.service.BoardService;
import com.example.redissessionclusteringindexpractice.service.CommentService;
import com.example.redissessionclusteringindexpractice.service.LikesService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class RedisTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private LikesService likesService;

    @Test
    @Disabled
    @DisplayName("분산락 테스트1")
    public void distributeLockTest()throws Exception{
        //시나리오 :: 게시글 조회시 조회수가 원하는 만큼 증가하는가??
        int numberOfThreads = 200;
        ExecutorService executorService = Executors.newFixedThreadPool(35);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(int i =0; i< numberOfThreads; i++){
            executorService.submit(()->{
                try{
                    boardService.boardDetail(1L);
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  
        //+1 인 이유
        /*BoardResponse response = boardService.boardDetail(1L);
        System.out.println(response);

        assertThat(response.getReadCount()).isEqualTo(201);*/
    }

    @Test
    @Disabled
    @DisplayName("분산락 테스트2")
    public void distributeTest2() throws InterruptedException {
        //게시글에 좋아요 증가/감소 테스트
        int numberOfThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for(int i =0; i<numberOfThreads;i++){
            executorService.submit(()->{
               try {
                   likesService.likPlus(3L,1L);
               }catch (Exception e) {
                   e.printStackTrace(); // 예외 처리
               } finally {
                   latch.countDown(); // 작업이 완료되면 countDown 호출
               }
            });
        }
        latch.await();
    }

    @Test
    @Disabled
    public void distributeTest3()throws InterruptedException{
        //게시글에 좋아요 증가/감소 테스트
        int numberOfThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i =0;i<numberOfThreads;i++){
            executorService.submit(()->{

            });
        }
    }
}
