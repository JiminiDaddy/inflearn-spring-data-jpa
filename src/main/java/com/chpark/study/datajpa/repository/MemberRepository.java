package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
