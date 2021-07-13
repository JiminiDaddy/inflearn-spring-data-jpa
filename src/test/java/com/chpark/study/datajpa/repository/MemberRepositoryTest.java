package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.domain.Team;
import com.chpark.study.datajpa.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private TeamRepository teamRepository;

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
}