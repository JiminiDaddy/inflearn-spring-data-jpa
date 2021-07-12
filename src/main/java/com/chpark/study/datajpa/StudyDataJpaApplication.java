package com.chpark.study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// SpringBoot를 사용한다면 생략 가능
@EnableJpaRepositories(basePackages = "com.chpark.study.datajpa.repository")
@SpringBootApplication
public class StudyDataJpaApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudyDataJpaApplication.class, args);
	}
}
