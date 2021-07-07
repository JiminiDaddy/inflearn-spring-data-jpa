package com.chpark.study.datajpa.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
public class Member {
	@Id @GeneratedValue
	private Long id;
	private String name;

	public Member(String name) {
		this.name = name;
	}
}
