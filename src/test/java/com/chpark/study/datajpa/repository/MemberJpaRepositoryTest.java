package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(false)
@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

	@Autowired
	private MemberJpaRepository memberJpaRepository;

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
}