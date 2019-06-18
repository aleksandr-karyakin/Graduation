package com.company.project.service;

import com.company.project.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import com.company.project.model.Dish;
import com.company.project.repository.RestaurantRepository;
import com.company.project.util.exception.NotFoundException;

import java.util.List;

import static com.company.project.util.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional(readOnly = true)
public class DishServiceImpl implements DishService {

    private static final Sort SORT_NAME = new Sort(Sort.Direction.ASC, "name");

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @Autowired
    public DishServiceImpl(RestaurantRepository restaurantRepository, DishRepository dishRepository) {
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    @Transactional
    public Dish create(Dish dish, int restaurantId) {
        Assert.notNull(dish, "dish must not be null");
        dish.setRestaurant(restaurantRepository.getOne(restaurantId));
        return dishRepository.save(dish);
    }

    @Override
    @Transactional
    public void update(Dish dish, int restaurantId) {
        Assert.notNull(dish, "dish must not be null");
        get(dish.getId(), restaurantId);
        dish.setRestaurant(restaurantRepository.getOne(restaurantId));
        dishRepository.save(dish);
    }

    @Override
    @Transactional
    public void delete(int id, int restaurantId) {
        checkNotFoundWithId(dishRepository.delete(id, restaurantId) != 0, id);
    }

    @Override
    public Dish get(int id, int restaurantId) {
        return checkNotFoundWithId(dishRepository.findByIdAndRestaurantId(id, restaurantId), id);
    }

    @Override
    public List<Dish> getAllForUser(int restaurantId) {
        return dishRepository.findAllByRestaurantIdAndEnabledTrue(SORT_NAME, restaurantId);
    }

    @Override
    public List<Dish> getAllForManager(int restaurantId) {
        return dishRepository.findAllByRestaurantId(SORT_NAME, restaurantId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void enable(int id, int restaurantId, boolean flag) {
        Dish dish = get(id, restaurantId);
        dish.setEnabled(flag);
    }

    @Override
    public void checkAccess(int id, int restaurantId, int managerId) {
        if (restaurantRepository.findByIdAndUserId(restaurantId, managerId) == null) {
            throw new NotFoundException("Not found entity with " + id);
        }
    }
}
