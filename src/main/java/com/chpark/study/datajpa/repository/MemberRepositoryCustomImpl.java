package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

// 스프링 데이터 2.x 이상부터는 JPARepository를 상속받은 Repository 인텉페이스 이름 + Impl 외에 한가지가 더 가능하다.
// 사용자정의 인터페이스명 + Impl도 지원한다.
// 즉, MemberRepositoryImpl, MemberRepositoryCustomImpl 2가지 네이밍이 가능하다.
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom{
	private final EntityManager entityManager;

	@Override
	public List<Member> findAllCustom() {
		return entityManager.createQuery("select m from Member m", Member.class)
			.getResultList();
	}
}
