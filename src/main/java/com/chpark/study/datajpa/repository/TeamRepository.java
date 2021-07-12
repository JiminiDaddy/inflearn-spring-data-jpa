package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
