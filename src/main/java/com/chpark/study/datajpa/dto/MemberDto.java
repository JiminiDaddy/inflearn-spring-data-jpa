package com.chpark.study.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@AllArgsConstructor
public class MemberDto {
	private Long memberId;
	private String memberName;
	private String teamName;
}
