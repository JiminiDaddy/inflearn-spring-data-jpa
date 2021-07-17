package com.chpark.study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// SpringBoot를 사용한다면 생략 가능
// 사용자정의 리포지터리 클래스의 네이밍을 Impl외에 다른걸로 쓰고싶으면 repositoryImplementationPostfix에 알맞는 postFix를 설정한다.
//@EnableJpaRepositories(basePackages = "com.chpark.study.datajpa.repository", repositoryImplementationPostfix = "Impls")
@SpringBootApplication
public class StudyDataJpaApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudyDataJpaApplication.class, args);
	}
}
