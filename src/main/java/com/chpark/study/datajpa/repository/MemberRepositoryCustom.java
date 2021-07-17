package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;

import java.util.List;

public interface MemberRepositoryCustom {
	List<Member> findAllCustom();
}
