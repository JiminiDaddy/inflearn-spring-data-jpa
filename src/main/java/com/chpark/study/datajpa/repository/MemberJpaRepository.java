package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

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

	public Optional<Member> findById(Long id) {
		Member member = entityManager.find(Member.class, id);
		return Optional.ofNullable(member);
	}

	public long count() {
		return entityManager.createQuery("select count(m) from Member m", Long.class).getSingleResult();
	}

	public List<Member> findAll() {
		return entityManager.createQuery("select m from Member m", Member.class)
			.getResultList();
	}

	public void delete(Member member) {
		entityManager.remove(member);
	}
}
