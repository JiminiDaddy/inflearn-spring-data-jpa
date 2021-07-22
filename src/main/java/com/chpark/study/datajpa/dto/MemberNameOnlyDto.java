package com.chpark.study.datajpa.dto;

public class MemberNameOnlyDto {
	private final String name;

	public MemberNameOnlyDto(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
