package com.workshop.events.service;

import com.workshop.events.dto.SubscriptionResponse;
import com.workshop.events.exception.SubscriptionConflictException;
import com.workshop.events.exception.EventNotFoundException;
import com.workshop.events.exception.UserIndicatorNotFoundException;
import com.workshop.events.model.Event;
import com.workshop.events.model.Subscription;
import com.workshop.events.model.User;
import com.workshop.events.repository.EventRepo;
import com.workshop.events.repository.SubscriptionRepo;
import com.workshop.events.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
        Subscription subs = new Subscription();
        Event evt = evtRepo.findByPrettyName(eventName);
        if(evt == null){
            throw new EventNotFoundException("Evento " +eventName+ " nao existe");
        }
        User userRec = userRepo.findByEmail(user.getEmail());
        if(userRec == null){
            userRec = userRepo.save(user);
        }

        User indicator = userRepo.findById(userId).orElse(null);
        if(indicator == null){
            throw new UserIndicatorNotFoundException("User " + userId + " nao existe");
        }

        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicator);

        Subscription tmpSub = subRepo.findByEventAndSubscriber(evt, userRec);
        if(tmpSub != null){
            throw new SubscriptionConflictException("Já existe inscrição para o usuário "+userRec.getEmail()+" no evento "+evt.getTitle());
        }

        Subscription res = subRepo.save(subs);
        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
    }
}
