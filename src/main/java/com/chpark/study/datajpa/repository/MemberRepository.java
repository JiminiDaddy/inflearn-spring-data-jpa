package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	// 이름이 name이면서 나이가 age보다 큰 경우를 조회한다
	List<Member> findByNameAndAgeGreaterThan(String name, int age);
	// 모든 멤버를 조회한다
	List<Member> findBy();

	@Query("select m from Member m where m.name = :name and m.age = :age")
	List<Member> findMember(@Param("name") String name, @Param("age") int age);

	// @Param 생략 가능
	//@Query("select m from Member m where m.name = :name and m.age = :age")
	//List<Member> findMember(String name, int age);
	@Query("select new com.chpark.study.datajpa.dto.MemberDto(m.id, m.name, t.name) from Member m join m.team t")
	List<MemberDto> findMemberDto();

	@Query("select m from Member m where m.name in :names")
	List<Member> findByNames(Collection<String> names);

	@Query("select m from Member m where m.name = :name")
	List<Member> findMembersByName(String name);

	@Query("select m from Member m where m.name = :name")
	Optional<Member> findOptionalMemberByName(String name);

	@Query("select m from Member m where m.name = :name")
	Member findMemberByName(String name);

	@Query("select m.name from Member m")
	List<String> findNames();

	Page<Member> findByAge(int age, Pageable pageable);

	@Query(value = "select m from Member m", countQuery = "select count(m.id) from Member m")
	Page<Member> findMemberAllCountBy(Pageable pageable);

	Slice<Member> findSlicedMemberByAge(int age, Pageable pageable);
}
