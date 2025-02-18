package com.workshop.events.repository;

import com.workshop.events.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepo extends CrudRepository<Event, Integer> {
    public Event findByPrettyName(String prettyName);
}
