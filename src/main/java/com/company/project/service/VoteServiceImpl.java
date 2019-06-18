package com.company.project.service;

import com.company.project.repository.UserRepository;
import com.company.project.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.model.Vote;
import com.company.project.repository.RestaurantRepository;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class VoteServiceImpl implements VoteService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public VoteServiceImpl(UserRepository userRep, RestaurantRepository restRep, VoteRepository voteRep) {
        this.userRepository = userRep;
        this.restaurantRepository = restRep;
        this.voteRepository = voteRep;
    }

    @Override
    @Transactional
    public Vote vote(int userId, int restaurantId) {
        Vote vote = voteRepository.findByUserIdAndDate(userId, LocalDate.now());
        if (vote != null) {
            vote.setRestaurant(restaurantRepository.getOne(restaurantId));
            return voteRepository.save(vote);
        } else {
            vote = new Vote(null, LocalDate.now());
            vote.setUser(userRepository.getOne(userId));
            vote.setRestaurant(restaurantRepository.getOne(restaurantId));
            return voteRepository.save(vote);
        }
    }

    @Override
    public int getCurrentRate(int restaurantId) {
        return voteRepository.countByRestaurantIdAndDate(restaurantId, LocalDate.now());
    }

    @Override
    public int getSelectedRestaurant(int userId) {
        Vote selected = voteRepository.findByUserIdAndDate(userId, LocalDate.now());
        return selected == null ? 0 : selected.getRestaurant() == null ? 0 : selected.getRestaurant().getId();
    }
}
