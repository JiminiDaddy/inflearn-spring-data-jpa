package com.chpark.study.datajpa.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class JpaBaseEntity {

	@Column(updatable = false)
	private LocalDateTime createdDate;

	private LocalDateTime lastModifiedDate;


	@PrePersist
	void prePersist() {
		createdDate = LocalDateTime.now();
		lastModifiedDate = createdDate;
	}

	@PreUpdate
	void preUpdate() {
		lastModifiedDate = LocalDateTime.now();
	}
}
