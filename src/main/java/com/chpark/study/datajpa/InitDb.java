package com.chpark.study.datajpa;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class InitDb {
	private final MemberRepository memberRepository;

	@PostConstruct
	public void init() {
		memberRepository.save(new Member("user1", 10));
		memberRepository.save(new Member("user2", 20));
		memberRepository.save(new Member("user3", 30));
		memberRepository.save(new Member("user4", 40));
	}
}
