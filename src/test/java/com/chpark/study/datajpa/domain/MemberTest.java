package com.chpark.study.datajpa.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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

	@Test
	@DisplayName("JpaBaseEntity")
	void jpaBaseEntity() throws InterruptedException {
		Member member1 = new Member("member1", 10);
		entityManager.persist(member1);

		Thread.sleep(100);
		member1.changeName("new_member1");

		entityManager.flush();
		entityManager.clear();

		Member findMember = entityManager.find(Member.class, member1.getId());
		System.out.println("created: " + findMember.getCreatedDate());
		System.out.println("updated: " + findMember.getLastModifiedDate());
		System.out.println("createdBy: " + findMember.getCreatedBy());
		System.out.println("updatedBy: " + findMember.getLastModifiedBy());
		assertThat(findMember.getCreatedDate()).isNotNull();
		assertThat(findMember.getLastModifiedDate()).isNotNull();
		assertThat(findMember.getCreatedBy()).isNotNull();
		assertThat(findMember.getLastModifiedBy()).isNotNull();

	}

}
