package com.chpark.study.datajpa.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Item implements Persistable<Long> {
	@Id
	private Long id;

	// persist가 완료되기전에 createdDate가 할당된다.
	@CreatedDate
	private LocalDateTime createdDate;

	public Item(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		// createdDate == null일 때는 엔티티가 처음으로 영속화 되었다고 가정할 수 있다.
		return createdDate == null;
	}
}
