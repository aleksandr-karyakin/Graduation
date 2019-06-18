package com.company.project.service;

import com.company.project.model.Dish;

import java.util.List;

public interface DishService {

    Dish create(Dish dish, int restaurantId);

    void update(Dish dish, int restaurantId);

    void delete(int id, int restaurantId);

    Dish get(int id, int restaurantId);

    List<Dish> getAllForUser(int restaurantId);

    List<Dish> getAllForManager(int restaurantId);

    void enable(int id, int restaurantId, boolean flag);

    void checkAccess(int id, int restaurantId, int managerId);
}
