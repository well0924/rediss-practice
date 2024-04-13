package com.example.redissessionclusteringindexpractice.domain.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.example.redissessionclusteringindexpractice.domain.dto.QMemberResponse is a Querydsl Projection type for MemberResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMemberResponse extends ConstructorExpression<MemberResponse> {

    private static final long serialVersionUID = -1320438881L;

    public QMemberResponse(com.querydsl.core.types.Expression<? extends com.example.redissessionclusteringindexpractice.domain.Member> member) {
        super(MemberResponse.class, new Class<?>[]{com.example.redissessionclusteringindexpractice.domain.Member.class}, member);
    }

}

