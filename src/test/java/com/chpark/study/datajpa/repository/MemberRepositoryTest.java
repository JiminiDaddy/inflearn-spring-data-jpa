package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.domain.Team;
import com.chpark.study.datajpa.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Rollback
@Transactional
@SpringBootTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TeamRepository teamRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
		teamRepository.deleteAll();
		// H2 DB를 사용하는 경우 DB를 아래와 같이 설정해주어야 Id값이 Auto-Increment될 때 다른 테스트에 영향주지않고 새로 시작할 수 있다.
		// 단 식별자 생성전략을 IDENTITY로 변경해주어야 한다. (SpringBoot 1.x와 2.x의 식별자 생성전략 기본값이 바뀜, default=TABLE방식)
		// property에서 hibernate의 새로운 식별자 생성 옵션을 false처리할 수도 있다. (spring.jpa.hibernate.use-new-id-generator-mappings: false)
		// 하지만 결국엔 SpringBoot가 Hibernate를 따라갈것이라 예상되므로 옵션으로 처리안하고 IDENTITY사용함 (이동욱님 블로그 참조)
		entityManager.createNativeQuery("ALTER TABLE MEMBER ALTER COLUMN member_id RESTART WITH 1").executeUpdate();
	}

	@Test
	void testMember() {
		Member member = new Member("memberA");
		Member savedMember = memberRepository.save(member);

		Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow(
			() -> new IllegalArgumentException("Wrong memberId:<" + savedMember.getId() + ">"));

		assertThat(findMember.getId()).isEqualTo(savedMember.getId());
		assertThat(findMember.getName()).isEqualTo(savedMember.getName());

		assertThat(findMember).isEqualTo(savedMember);
		assertThat(findMember).isSameAs(savedMember);
	}

	@Test
	@DisplayName("Member CRUD 기능의 동작 확인")
	public void basicCRUD() {
		Member member1 = new Member("member1");
		Member member2 = new Member("member2");
		memberRepository.save(member1);
		memberRepository.save(member2);

		// 단건 조회 검증
		Member findMember1 = memberRepository.findById(member1.getId()).get();
		Member findMember2 = memberRepository.findById(member2.getId()).get();
		assertThat(findMember1).isSameAs(member1);
		assertThat(findMember2).isSameAs(member2);

		findMember1.changeName("newMember1");

		// 리스트 조회 검증
		List<Member> members = memberRepository.findAll();
		assertThat(members.size()).isEqualTo(2);

		// 카운트 검증
		long count = memberRepository.count();
		assertThat(count).isEqualTo(2);

		// 삭제 검증
		memberRepository.delete(member1);
		memberRepository.delete(member2);
		long deletedCount = memberRepository.count();
		assertThat(deletedCount).isEqualTo(0);
	}

	@Test
	@DisplayName("이름과 나이 조건으로 조회")
	public void findByNameAndAgeGreaterThan() {
		Member member1 = new Member("user1", 30);
		Member member2 = new Member("user2", 10);
		Member member3 = new Member("user2", 40);

		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		List<Member> members = memberRepository.findByNameAndAgeGreaterThan("user2", 20);
		assertThat(members.size()).isEqualTo(1);
		assertThat(members.get(0).getName()).isEqualTo(member3.getName());
		assertThat(members.get(0).getAge()).isEqualTo(member3.getAge());

		List<Member> list = memberRepository.findBy();
		System.out.println(list.size());
	}

	@Test
	@DisplayName("@Query 멤버 조회")
	void findMemberWithQuery() {
		Member member1 = new Member("user1", 30);
		Member member2 = new Member("user2", 10);
		Member member3 = new Member("user2", 40);

		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		List<Member> findMembers = memberRepository.findMember("user2", 40);
		assertThat(findMembers.size()).isEqualTo(1);
		assertThat(findMembers.get(0)).isSameAs(member3);
	}

	@Test
	@DisplayName("Dto로 조회")
	void findMemberDto() {
		Team team = new Team("team1");
		teamRepository.save(team);

		Member member1 = new Member("user1", 30);
		member1.changeTeam(team);
		memberRepository.save(member1);

		List<MemberDto> memberDtos = memberRepository.findMemberDto();
		assertThat(memberDtos.size()).isEqualTo(1);
		for (MemberDto memberDto : memberDtos) {
			System.out.println("memberDto = " + memberDto);
		}
	}

	@Test
	@DisplayName("컬렉션을 in절로 만들어 name이 포함된 멤버 조회")
	void findByNames() {
		Member member1 = new Member("user1", 30);
		Member member2 = new Member("user2", 20);
		memberRepository.save(member1);
		memberRepository.save(member2);

		List<Member> findMembers = memberRepository.findByNames(Arrays.asList("user1", "user11", "user21"));
		assertThat(findMembers.size()).isEqualTo(1);
		for (Member findMember : findMembers) {
			System.out.println("findMember = " + findMember);
		}
	}

	@Test
	@DisplayName("멤버 조회 결과를 Collection, Optional, Entity로 반환")
	void findMembersByName() {
		Member member1 = new Member("user1", 30);
		Member member2 = new Member("user2", 20);
		Member member3 = new Member("user2", 50);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		List<Member> findMembers = memberRepository.findMembersByName("user2");
		assertThat(findMembers.size()).isEqualTo(2);
		for (Member findMember : findMembers) {
			System.out.println("findMember = " + findMember);
		}

		findMembers = memberRepository.findMembersByName("user4");
		assertThat(findMembers.size()).isEqualTo(0);
		assertThat(findMembers.size()).isNotNull();
		System.out.println("findMembers = " + findMembers);

		Optional<Member> findOptionalMember = memberRepository.findOptionalMemberByName("user1");
		assertThat(findOptionalMember.get().getName()).isEqualTo(member1.getName());

		Member findMember = memberRepository.findMemberByName("user1");
		assertThat(findMember).isNotNull();

		assertThatThrownBy(() -> {
			Member find = memberRepository.findMemberByName("user2");
		}).isInstanceOf(IncorrectResultSizeDataAccessException.class);	// NonUniqueResultException을 Spring이 한번 감싸 새로운 예외 던짐
	}

	@Test
	@DisplayName("멤버 이름 목록조회")
	void findNames() {
		Member member1 = new Member("user1", 30);
		Member member2 = new Member("user2", 20);
		Member member3 = new Member("user3", 50);
		Member member4 = new Member("user4", 10);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);

		List<String> memberNames = memberRepository.findNames();
		assertThat(memberNames.size()).isEqualTo(4);
		System.out.println(Arrays.toString(memberNames.toArray()));
	}

	@Test
	@DisplayName("페이징")
	void findByAgeWithPage() {
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamA);
		Member member3 = new Member("member3", 10, teamA);
		Member member4 = new Member("member4", 10, teamA);
		Member member5 = new Member("member5", 10, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
		memberRepository.save(member5);

		int age = 10;
		int offset = 0;
		int limit = 3;
		// Pageable에서의 offset은 전체 데이터에서의 Index가 아니다.
		// 2번째 파라미터인 limit에 따라 offset이 가리키는 데이터의 Index가 변화한다.
		// 예를들어 데이터가 100개있고, offset=0, limit=10인경우 페이징된 데이터의 1번째 Index는 0이다.
		// 그리고 offset=1, limit=10인경우 페이징된 데이터의 1번째 Index는 10이 된다. (offset=0이 0~9번째 데이터를 갖고있기때문)
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "name"));
		Page<Member> pages = memberRepository.findByAge(age, pageRequest);

		List<Member> contents = pages.getContent();			// 페이징된 데이터
		System.out.println(Arrays.toString(contents.toArray()));
		assertThat(contents.size()).isEqualTo(3);			// 페이징된 데이터의 개수
		assertThat(pages.getTotalElements()).isEqualTo(5);	// 전체 데이터 수
		assertThat(pages.getNumber()).isEqualTo(0);			// 페이지 번호
		assertThat(pages.isFirst()).isTrue();				// 첫 페이지 1번인가?
		assertThat(pages.hasNext()).isTrue();				// 다음 페이지가 있는가?
		assertThat(pages.isLast()).isFalse();				// 마지막 페이지인가?
		assertThat(pages.hasPrevious()).isFalse();			// 이전 페이지가 존재하는가?
	}

	@Test
	@DisplayName("슬라이싱")
	void findSlicedByAge() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		Member member3 = new Member("member3", 10);
		Member member4 = new Member("member4", 10);
		Member member5 = new Member("member5", 10);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
		memberRepository.save(member5);

		int age = 10;
		int offset = 1;
		int limit = 3;
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.Direction.DESC, "name");

		Slice<Member> slicedMembers = memberRepository.findSlicedMemberByAge(age, pageRequest);
		List<Member> members = slicedMembers.getContent();

		assertThat(members.size()).isEqualTo(2);		// 5 - 3
		assertThat(slicedMembers.hasNext()).isFalse();
		assertThat(slicedMembers.isFirst()).isFalse();
		assertThat(slicedMembers.isLast()).isTrue();
		assertThat(slicedMembers.getNumber()).isEqualTo(1);
	}

	@Test
	@DisplayName("카운트 분리")
	void count() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 10);
		Member member3 = new Member("member3", 10);
		Member member4 = new Member("member4", 10);
		Member member5 = new Member("member5", 10);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
		memberRepository.save(member5);

		int age = 10;
		int offset = 1;
		int limit = 3;
		PageRequest pageRequest = PageRequest.of(offset, limit, Sort.Direction.DESC, "name");

		Page<Member> pages = memberRepository.findMemberAllCountBy(pageRequest);
		System.out.println(pages.getTotalElements());
	}

	@Test
	@DisplayName("벌크성 수정쿼리")
	void blukAgePlus() {
		Member member1 = new Member("member1", 10);
		Member member2 = new Member("member2", 20);
		Member member3 = new Member("member3", 30);
		Member member4 = new Member("member4", 40);
		Member member5 = new Member("member5", 50);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
		memberRepository.save(member5);

		int count = memberRepository.blukAgePlus(30);
		assertThat(count).isEqualTo(3);

		// 이렇게 테스트해야 다른 테스트에 영향 받지않고 실제 member5의 Id를 통해 테스트를 수행할 수 있다.
		//Member findMember = memberRepository.findById(member5.getId()).orElseThrow(() -> new IllegalArgumentException("Wrong"));

		// 만약 아래와 같이 Id값을 Fix하여 테스트할 경우, 다른 테스트에 영향이 없도록 DB설정을 해주어야 한다.
		// @After 부분 참고
		Member findMember = memberRepository.findById(5L).get();
		assertThat(findMember.getAge()).isEqualTo(51);
	}

	@Test
	@DisplayName("EntityGraph")
	void finMemberLazy() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		// Member.Team이 Lazy로 관계가 맺어저있으므로, member수에 따른 N+1 이슈가 발생한다.
		// List<Member> findMembers = memberRepository.findAll();

		// 직접 JPQL 작성하여 조회
		// List<Member> findMembers = memberRepository.findMembersFetchJoin();

		// @EntityGraph 애너테이션 사용
		// 기본적으로 left outer join을 사용하며, 내부적으로 fetch join을 사용한다.
		// List<Member> findMembers = memberRepository.findMembersEntityGraph();
		// List<Member> findMembers = memberRepository.findAll();

		// QueryMethod + @EntityGraph
		List<Member> findMembers = memberRepository.findByName("member1");

		for (Member findMember : findMembers) {
			System.out.println("findMember.getName = " + findMember.getName());
			System.out.println("findMember.getTeam = " + findMember.getTeam().getName());
			System.out.println("findMember.Team.class = " + findMember.getTeam().getClass());
		}
	}

	@Test
	@DisplayName("JPA Hints")
	void findHintByName() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		entityManager.flush();
		entityManager.clear();

		Member findMember = memberRepository.findHintByName("member1").get(0);
		assertThat(findMember.getName()).isEqualTo("member1");
		// 힌트에 의해 readOnly가 적용되어 update sql은 생성되지 않는다.
		findMember.changeName("new_member1");

		entityManager.flush();
		Member findMember2 = memberRepository.findByName("member1").get(0);
		//assertThat(findMember2.getName()).isEqualTo("new_member1");
	}

	@Test
	@DisplayName("사용자정의리포지토리")
	void findAllWithCusotmRepository() {
		Member member1 = new Member("member1", 10);
		memberRepository.save(member1);
		entityManager.flush();
		entityManager.clear();

		List<Member> findMembers = memberRepository.findAllCustom();
		assertThat(findMembers.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("인터페이스 기반 Closed-Projections")
	void projectionsByInterface() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		List<MemberNameOnly> result = memberRepository.findProjectionsByName("member2");
		assertThat(result.size()).isEqualTo(1);
		for (MemberNameOnly memberNameOnly : result) {
			System.out.println("memberNameOnly = " + memberNameOnly.getName());
		}
	}

	@Test
	@DisplayName("클래스 기반 Closed-Projections")
	void projectionsByClass() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		List<MemberNameOnlyDto> result = memberRepository.findProjectionsClassByName("member2");
		assertThat(result.size()).isEqualTo(1);
		for (MemberNameOnlyDto memberNameOnly : result) {
			System.out.println("memberNameOnly = " + memberNameOnly.getName());
		}
	}

	@Test
	@DisplayName("제네릭 기반 Closed-Projections")
	void projectionsByGeneric() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		List<MemberNameOnly> result = memberRepository.findProjectionsGenericByName("member2", MemberNameOnly.class);
		assertThat(result.size()).isEqualTo(1);
		for (MemberNameOnly memberNameOnly : result) {
			System.out.println("memberNameOnly = " + memberNameOnly.getName());
		}
	}

	@Test
	@DisplayName("중첩구조의 Closed-Projections")
	void projectionsByNested() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		List<NestedClosedProjection> result = memberRepository.findProjectionsNestedByName("member2");
		assertThat(result.size()).isEqualTo(1);
		for (NestedClosedProjection memberNameOnly : result) {
			System.out.println("memberNameOnly.member = " + memberNameOnly.getName());
			System.out.println("memberNameOnly.team = " + memberNameOnly.getTeam().getName());
		}
	}

	@Test
	@DisplayName("NativeQuery + Projection")
	void nativeQueryWithProjection() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10,  Sort.by(Sort.Direction.ASC, "name")));
		List<MemberProjection> content = result.getContent();
		for (MemberProjection memberProjection : content) {
			System.out.println("member_id   = " + memberProjection.getMemberId());
			System.out.println("member_name = " + memberProjection.getMemberName());
			System.out.println("team_name   = " + memberProjection.getTeamName());
			System.out.println("===========");
		}
	}

	@Test
	@DisplayName("fetch join")
	void fetchJoin() {
		Team teamA = new Team("TeamA");
		Team teamB = new Team("TeamB");
		teamRepository.save(teamA);
		teamRepository.save(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamA);
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);

		entityManager.flush();
		entityManager.clear();

		List<Member> membersFetchJoin = memberRepository.findMembersFetchJoin();
	}
}