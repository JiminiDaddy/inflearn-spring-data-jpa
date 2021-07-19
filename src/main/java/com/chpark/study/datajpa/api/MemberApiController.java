package com.chpark.study.datajpa.api;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberApiController {
	private final MemberRepository memberRepository;

	@GetMapping("/api/v1/members/{id}")
	public String findMember(@PathVariable("id") Long id) {
		// OSIV를 열어두었으므로 Controller에서 영속성 컨텍스트를 통해 엔티티를 조회할 수 있다.
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Wrong id: <" + id + ">"));
		return member.getName();
	}

	// for Domain-Class-Converter
	@GetMapping("/api/v1/members2/{id}")
	public String findMember2(@PathVariable("id") Member member) {
		return member.getName();
	}
}
