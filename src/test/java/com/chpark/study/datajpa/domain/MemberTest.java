package com.chpark.study.datajpa.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberTest {
	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void testEntity() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		entityManager.persist(teamA);
		entityManager.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		entityManager.persist(member1);
		entityManager.persist(member2);
		entityManager.persist(member3);
		entityManager.persist(member4);

		// 초기화
		entityManager.flush();	// 영속성 컨텍스트와 데이터베이스를 동기화시킨다. (SQL 쓰기지연 저장소에 있는 Query를 DB로 전송한다)
		entityManager.clear();

		List<Member> members = entityManager.createQuery("select m from Member m", Member.class).getResultList();
		for (Member member : members) {
			System.out.println("member = " + member);
			System.out.println("-> member.team = " + member.getTeam());
		}
	}

}
