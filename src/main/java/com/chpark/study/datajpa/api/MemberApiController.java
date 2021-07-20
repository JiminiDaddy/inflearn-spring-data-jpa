package com.chpark.study.datajpa.api;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.dto.MemberDto;
import com.chpark.study.datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

	// 실무에선 엔티티를 절대로 반환하면 안된다. 반드시 API는 Dto를 전달하도록 한다.
	// call exam: /api/v1/members?page=1&size=5&sort=age,desc
	// InitDb에서 7/19 기준 Team 엔티티를 추가하여 연관관계를 맺도록 구성하였으므로, 아래 API를 호출할경우 순환참조 오류 발생한다.
	// Dto를 쓰던 JsonIgnore로 막아주던 해야한다. 그냥 호출하면 안됨!!
	@GetMapping("/api/v1/members")
	public Page<Member> findMember3(@PageableDefault(size = 5, page = 0, sort = "age", direction = Sort.Direction.DESC) Pageable pageable) {
		return memberRepository.findAll(pageable);
	}

	// Dto로 변환하였으므로 위 API처럼 순환참조는 발생하지 않는다.
	@GetMapping("/api/v2/members")
	public Page<MemberDto> findMember4(Pageable pageable) {
		return memberRepository.findAll(pageable).map(MemberDto::new);
	}
}
