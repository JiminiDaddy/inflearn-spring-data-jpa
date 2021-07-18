package com.chpark.study.datajpa.domain;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
// 등록일/수정일과 등록자/수정자를 분리하여 각 도메인에서 필요에 따라 확장할 수 있도록 구현함
public class BaseEntity extends BaseTimeEntity {
	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;
}
