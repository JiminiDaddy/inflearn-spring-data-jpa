package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
