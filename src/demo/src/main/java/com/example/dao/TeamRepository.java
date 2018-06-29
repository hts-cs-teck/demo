package com.example.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("from Team t where t.parentid in (:parentids) order by t.id")
	public List<Team> findAnyCondByParentids(@Param("parentids") Set<Long> parentids);

    @Query("from Team t where t.name like %?1% order by t.id")
	public List<Team> findByNameLike(String name);
	public List<Team> findByParentidIsNull();
	public List<Team> findById(@Param("id") Long id);
	public List<Team> findByParentid(@Param("parentid") Long parentid);
	public List<Team> findByIdAndParentid(@Param("id") Long id, @Param("parentid") Long parentid);
	public List<Team> findByIdAndParentidIsNull(@Param("id") Long id);

}