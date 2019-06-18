package com.company.project.service;

import com.company.project.model.Vote;

public interface VoteService {

    Vote vote(int userId, int restaurantId);

    int getCurrentRate(int restaurantId);

    int getSelectedRestaurant(int userId);
}
