package com.example.redissessionclusteringindexpractice;

import com.example.redissessionclusteringindexpractice.domain.Board;
import com.example.redissessionclusteringindexpractice.domain.Member;
import com.example.redissessionclusteringindexpractice.repository.BoardRepository;
import com.example.redissessionclusteringindexpractice.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

@EnableJpaAuditing
@SpringBootApplication
public class RedisSessionClusteringIndexPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisSessionClusteringIndexPracticeApplication.class, args);
    }

}
