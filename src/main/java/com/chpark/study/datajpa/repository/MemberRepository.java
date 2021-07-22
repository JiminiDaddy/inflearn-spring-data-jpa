package com.chpark.study.datajpa.repository;

import com.chpark.study.datajpa.domain.Member;
import com.chpark.study.datajpa.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	// 이름이 name이면서 나이가 age보다 큰 경우를 조회한다
	List<Member> findByNameAndAgeGreaterThan(String name, int age);
	// 모든 멤버를 조회한다
	List<Member> findBy();

	@Query("select m from Member m where m.name = :name and m.age = :age")
	List<Member> findMember(@Param("name") String name, @Param("age") int age);

	// @Param 생략 가능
	//@Query("select m from Member m where m.name = :name and m.age = :age")
	//List<Member> findMember(String name, int age);
	@Query("select new com.chpark.study.datajpa.dto.MemberDto(m.id, m.name, 0, t.name) from Member m join m.team t")
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

	@Modifying(clearAutomatically = true)	// 벌크 쿼리 실행 후 영속성컨텍스트를 자동으로 초기화
	@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
	int blukAgePlus(int age);

	@Query("select m from Member m join fetch m.team")
	List<Member> findMembersFetchJoin();

	@Override
	@EntityGraph(attributePaths = {"team"})
	List<Member> findAll();

	@EntityGraph(attributePaths = {"team"})
	@Query("select m from Member m")
	List<Member> findMembersEntityGraph();

	@EntityGraph(attributePaths = {"team"})
	List<Member> findByName(String name);

	@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
	List<Member> findHintByName(String name);

	List<MemberNameOnly> findProjectionsByName(String name);

	List<MemberNameOnlyDto> findProjectionsClassByName(String name);

	<T> List<T> findProjectionsGenericByName(String name, Class<T> type);

	List<NestedClosedProjection> findProjectionsNestedByName(String name);

	@Query(value = "select m.MEMBER_ID as MemberId, m.NAME as MemberName, t.NAME as TeamName" +
		" from Member m inner join Team t on m.team_id = t.team_id",
		countQuery = "select count(m.member_id) from Member m",
		nativeQuery = true)
	Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
