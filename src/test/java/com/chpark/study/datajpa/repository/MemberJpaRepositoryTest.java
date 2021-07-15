package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

	@Autowired
	private MemberJpaRepository memberJpaRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberJpaRepository.save(member);

		Member findMember = memberJpaRepository.find(savedMember.getId());
		assertThat(findMember.getName()).isEqualTo(member.getName());
		assertThat(findMember.getId()).isEqualTo(member.getId());

		assertThat(findMember).isEqualTo(member);
		assertThat(findMember).isSameAs(member);
	}

	@Test
	@DisplayName("Member CRUD 기능의 동작 확인")
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);

		// 단건 조회 검증
		Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
		Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
		assertThat(findMember1).isSameAs(member1);
		assertThat(findMember2).isSameAs(member2);

		findMember1.changeName("newMember1");

		// 리스트 조회 검증
		List<Member> members = memberJpaRepository.findAll();
		assertThat(members.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberJpaRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberJpaRepository.delete(member1);
		memberJpaRepository.delete(member2);
		long deletedCount = memberJpaRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}

	@Test
	@DisplayName("페이징 기능")
	void findPaging() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		Member member3 = new Member("member3", 10);
		Member member4 = new Member("member4", 10);
		Member member5 = new Member("member5", 20);
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		memberJpaRepository.save(member3);
		memberJpaRepository.save(member4);
		memberJpaRepository.save(member5);

		int age = 10;
		int offset = 1;
		int limit = 3;

		List<Member> findMembers = memberJpaRepository.findPaging(age, offset, limit);
		long totalCount = memberJpaRepository.getTotalCount(age);
		assertThat(findMembers.size()).isEqualTo(3);
		assertThat(totalCount).isEqualTo(4);
	}

	@Test
	@DisplayName("벌크성 수정쿼리")
	void bulkAgePlus() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		Member member3 = new Member("member3", 30);
		Member member4 = new Member("member4", 40);
		Member member5 = new Member("member5", 50);
		memberJpaRepository.save(member1);
		memberJpaRepository.save(member2);
		memberJpaRepository.save(member3);
		memberJpaRepository.save(member4);
		memberJpaRepository.save(member5);

		int count = memberJpaRepository.bulkAgePlus(30);
		entityManager.clear();
		assertThat(count).isEqualTo(3);
		// 벌크성 쿼리는 영속성 컨텍스트에 반영하지 않고 즉시 JPQL을 SQL로 변환하여 DB로 전송한다.
		// 따라서 위 예제와 같이 age + 1한 결과가 DB에는 반영되었지만 영속성 컨텍스트에는 +1하기 전 원래 값이 들어있다.
		// 그 이유는 영속성 컨텍스트에 save(persist)했지만 아직 flush되진 않아 SQL 쓰기지연 저장소에 남아있기 때문이다.
		// 따라서 위와 같이 벌크성 쿼리를 실행한 후에는 EntityManager를 clear 시켜 쓰기지연 저장소에 남아있는 SQL을 비워 영속성 컨텍스트와 DB를 동기화시켜야 한다.
		System.out.println(memberJpaRepository.findById(5L));
	}
}