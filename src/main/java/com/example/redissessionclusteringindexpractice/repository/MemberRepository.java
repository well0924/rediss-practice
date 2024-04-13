package com.example.redissessionclusteringindexpractice.repository;

import com.example.redissessionclusteringindexpractice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Member findByUserId(String userId);
}
