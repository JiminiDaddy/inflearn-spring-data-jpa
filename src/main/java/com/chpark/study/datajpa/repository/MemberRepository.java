package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
	// 이름이 name이면서 나이가 age보다 큰 경우를 조회한다
	List<Member> findByNameAndAgeGreaterThan(String name, int age);
	// 모든 멤버를 조회한다
	List<Member> findBy();
}
