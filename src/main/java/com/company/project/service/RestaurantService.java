package com.company.project.service;

import com.company.project.model.Restaurant;

import java.util.List;

public interface RestaurantService {

    Restaurant create(Restaurant restaurant, int managerId);

    void update(Restaurant restaurant, int managerId);

    void delete(int id, int managerId);

    Restaurant get(int id, int managerId);

    List<Restaurant> getAllForUser();

    List<Restaurant> getAllForManager(int managerId);
}
