package com.chpark.study.datajpa;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.domain.Team;
import com.chpark.study.datajpa.repository.MemberRepository;
import com.chpark.study.datajpa.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class InitDb {
	private final MemberRepository memberRepository;
	private final TeamRepository teamRepository;

	@PostConstruct
	public void init() {
		Team team = new Team("teamA");
		teamRepository.save(team);

		for (int i = 1; i <= 100; ++i) {
			memberRepository.save(new Member("user" + i, i, team));
		}
	}
}
