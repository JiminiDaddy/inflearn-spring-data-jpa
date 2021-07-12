package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(false)
@Transactional
@SpringBootTest
class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepository;

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
}