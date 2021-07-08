package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

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
}