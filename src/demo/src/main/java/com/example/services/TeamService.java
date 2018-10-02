package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.TeamRepository;
import com.example.entity.Team;

@Service
@Transactional
public class TeamService {

	@Autowired
	TeamRepository repository;

	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findAll() {
		return repository.findAll();
	}

	@Transactional(readOnly = true, timeout = 10)
	public Team find(final Long id) {
		return repository.findOne(id);
	}

	@Transactional(rollbackFor = {Exception.class}, timeout = 3)
	public Team save(final Team team) {
		return repository.save(team);
	}

	@Transactional(rollbackFor = {Exception.class}, timeout = 3)
	public void delete(final Long id) {
		repository.delete(id);
	}

	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findByNameLike(final String name) {
		return repository.findByNameLike(name);
	}
	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findByParentidIsNull() {
		return repository.findByParentidIsNull();
	}
	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findById(final Long id) {
		return repository.findById(id);
	}
	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findByParentid(final Long parentid) {
		return repository.findByParentid(parentid);
	}
	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findByIdAndParentid(final Long id, final Long parentid) {
		return repository.findByIdAndParentid(id, parentid);
	}
	@Transactional(readOnly = true, timeout = 10)
	public List<Team> findByIdAndParentidIsNull(final Long id) {
		return repository.findByIdAndParentidIsNull(id);
	}

}