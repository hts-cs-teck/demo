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
}