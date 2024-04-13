package com.example.redissessionclusteringindexpractice.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.redissessionclusteringindexpractice.domain.dto.QBoardResponse is a Querydsl Projection type for BoardResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QBoardResponse extends ConstructorExpression<BoardResponse> {

    private static final long serialVersionUID = -1060679133L;

    public QBoardResponse(com.querydsl.core.types.Expression<? extends com.example.redissessionclusteringindexpractice.domain.Board> board) {
        super(BoardResponse.class, new Class<?>[]{com.example.redissessionclusteringindexpractice.domain.Board.class}, board);
    }

}

