package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ItemRepositoryTest {

	@Autowired
	private ItemRepository itemRepository;

	@Test
	@DisplayName("Save")
	void save() {
		Item item = new Item(1001L);
		itemRepository.save(item);
	}
}