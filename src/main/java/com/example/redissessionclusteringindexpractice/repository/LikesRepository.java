package com.example.redissessionclusteringindexpractice.repository;

import com.example.redissessionclusteringindexpractice.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface LikesRepository extends JpaRepository<Likes,Long> {

    //좋아요 중복처리
    @Query(value = "select l.id from Likes l where l.board.id = :boardId and l.member.id = :memberId")
    boolean findByBoardAndMember(@Param("boardId") Long boardId,@Param("memberId") Long memberId);

}
