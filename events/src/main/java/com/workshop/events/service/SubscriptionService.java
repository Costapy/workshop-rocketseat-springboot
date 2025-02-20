package com.workshop.events.service;

import com.workshop.events.dto.SubscriptionRankingByUser;
import com.workshop.events.dto.SubscriptionRankingItem;
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

import java.util.List;
import java.util.stream.IntStream;

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

        User indicator = null;
        if(userId != null) {
            indicator = userRepo.findById(userId).orElse(null);
            if(indicator == null){
                throw new UserIndicatorNotFoundException("User " + userId + " nao existe");
            }
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

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event evt = evtRepo.findByPrettyName(prettyName);
        if(evt == null){
            throw new EventNotFoundException("Evento " +prettyName + " nao existe");
        }
        return subRepo.generateRanking(evt.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem rankingItem = ranking.stream().filter(i->i.userId().equals(userId)).findFirst().orElse(null);
        if(rankingItem == null){
            throw new UserIndicatorNotFoundException("User " + userId + " nao existe");
        }
        Integer position = IntStream.range(0, ranking.size())
                .filter(pos->ranking.get(pos).userId().equals(userId))
                .findFirst().getAsInt();

        return new SubscriptionRankingByUser(rankingItem, position+1);
    }
}
