package com.workshop.events.repository;

import com.workshop.events.model.Event;
import com.workshop.events.model.Subscription;
import com.workshop.events.model.User;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepo extends CrudRepository<Subscription, Integer> {
    public Subscription findByEventAndSubscriber(Event event, User user);
}
