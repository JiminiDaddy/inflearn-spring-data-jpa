package com.chpark.study.datajpa.dto;

import com.chpark.study.datajpa.domain.Member;
import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class MemberDto {
	private Long memberId;
	private String memberName;
	private int memberAge;
	private String teamName;

	public MemberDto(Member member) {
		this.memberId = member.getId();
		this.memberName = member.getName();
		this.teamName = member.getTeam().getName();
		this.memberAge = member.getAge();
	}
}
