package com.chpark.study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
// SpringBoot를 사용한다면 생략 가능
// 사용자정의 리포지터리 클래스의 네이밍을 Impl외에 다른걸로 쓰고싶으면 repositoryImplementationPostfix에 알맞는 postFix를 설정한다.
//@EnableJpaRepositories(basePackages = "com.chpark.study.datajpa.repository", repositoryImplementationPostfix = "Impls")
@SpringBootApplication
public class StudyDataJpaApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudyDataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		// 실제로는 세션을 주입받아 세션정보를 기록해야 한다. (또는 스프링 시큐리티를 쓰면 시큐리티 정보를 사용)
		// 예제에서는 단순히 UUID값을 설정해주도록 구현함
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
