package com.example.redissessionclusteringindexpractice.repository.queryDsl;

import com.example.redissessionclusteringindexpractice.domain.QBoard;
import com.example.redissessionclusteringindexpractice.domain.QMember;
import com.example.redissessionclusteringindexpractice.domain.dto.BoardResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CustomBoardRepositoryImpl implements CustomBoardRepository{

    private final JPAQueryFactory jpaQueryFactory;

    QMember qMember;

    QBoard qBoard;

    public CustomBoardRepositoryImpl(EntityManager em){
        this.qBoard = QBoard.board;
        this.qMember = QMember.member;
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<BoardResponse> boardPaging(Pageable pageable) {

        JPQLQuery<BoardResponse>list = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,qBoard))
                .from(qBoard)
                .orderBy(qBoard.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());

        return PageableExecutionUtils.getPage(list.fetch(),pageable,list::fetchCount);
    }

    @Override
    public List<BoardResponse> boardResponseList(Long size, Long lastBoardId) {
        List<BoardResponse>boardResponseList = jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,qBoard))
                .from(qBoard)
                .where(ltBoardId(lastBoardId))
                .orderBy(qBoard.id.desc())
                .limit(size)
                .fetch();
        return boardResponseList;
    }

    @Override
    public BoardResponse boardDetail(Long boardId) {
        BoardResponse detail = jpaQueryFactory
                .select(Projections
                        .constructor(BoardResponse.class,qBoard))
                .from(qBoard)
                .where(qBoard.id.eq(boardId))
                .fetchOne();
        return detail;
    }

    @Override
    @Modifying
    @Transactional
    public void readCountUp(Long boardId) {
        jpaQueryFactory.update(qBoard)
                .set(qBoard.readCount,qBoard.readCount.add(1))
                .where(qBoard.id.eq(boardId))
                .execute();
    }

    private BooleanExpression ltBoardId(Long boardId) {
        if (boardId == null) {
            return null;
        }
        return qBoard.id.lt(boardId);
    }
}
