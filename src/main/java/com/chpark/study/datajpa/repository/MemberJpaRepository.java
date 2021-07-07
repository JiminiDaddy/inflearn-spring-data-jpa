package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberJpaRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public Member save(Member member) {
		entityManager.persist(member);
		return member;
	}

	public Member find(Long id) {
		return entityManager.find(Member.class, id);
	}
}
