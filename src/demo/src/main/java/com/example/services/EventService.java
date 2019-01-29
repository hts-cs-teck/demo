package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.EventRepository;
import com.example.entity.Event;

@Service
@Transactional
public class EventService {

	@Autowired
	EventRepository repository;

	@Transactional(readOnly = true, timeout = 10)
	public List<Event> findAll() {
		return repository.findAll(new Sort(Direction.ASC,"id"));
	}

	@Transactional(readOnly = true, timeout = 10)
	public Event find(final Long id) {
		return repository.findOne(id);
	}

	@Transactional(rollbackFor = {Exception.class}, timeout = 3)
	public Event save(final Event event) {
		return repository.save(event);
	}

	@Transactional(rollbackFor = {Exception.class}, timeout = 3)
	public void delete(final Long id) {
		repository.delete(id);
	}
}