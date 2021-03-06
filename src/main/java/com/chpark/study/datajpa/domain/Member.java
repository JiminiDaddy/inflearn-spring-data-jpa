package com.chpark.study.datajpa.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@ToString(of = {"id", "name", "age"})		// 연관관계 매핑된 엔티티 필드는 toString에 추가하지 않는다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
//public class Member extends JpaBaseEntity{		// 순수 JPA를 사용
public class Member extends BaseEntity {			// SpringDataJpa를 사용
	//@Id @GeneratedValue
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")		// 관례상 ColumnName = 테이블명_필드명으로 구성한다.
	private Long id;

	private String name;

	private int age;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")	// FK 이름을 지정한다.
	private Team team;

	public Member(String name) {
		this.name = name;
	}

	public Member(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Member(String name, int age, Team team) {
		this.name = name;
		this.age = age;
		changeTeam(team);
	}

	public void changeTeam(Team team) {
		if (this.team != null) {
			this.team.getMembers().remove(this);
		}
		this.team = team;
		team.getMembers().add(this);
	}

	public void changeName(String name) {
		this.name = name;
	}
}
