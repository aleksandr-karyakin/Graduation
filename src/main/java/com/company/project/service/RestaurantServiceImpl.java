package com.company.project.service;

import com.company.project.model.Restaurant;
import com.company.project.repository.RestaurantRepository;
import com.company.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.company.project.util.ValidationUtil.checkNotFoundWithId;

@Service
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private static final Sort SORT_NAME = new Sort(Sort.Direction.ASC, "name");

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public Restaurant create(Restaurant restaurant, int managerId) {
        Assert.notNull(restaurant, "restaurant must not be null");
        restaurant.setUser(userRepository.getOne(managerId));
        return restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void update(Restaurant restaurant, int managerId) {
        Assert.notNull(restaurant, "restaurant must not be null");
        get(restaurant.getId(), managerId);
        restaurant.setUser(userRepository.getOne(managerId));
        restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(int id, int managerId) {
        checkNotFoundWithId(restaurantRepository.delete(id, managerId) != 0, id);
    }

    @Override
    public Restaurant get(int id, int managerId) {
        return checkNotFoundWithId(restaurantRepository.findByIdAndUserId(id, managerId), id);
    }

    @Override
    @Cacheable("restaurants")
    public List<Restaurant> getAllForUser() {
        return restaurantRepository.findAll(SORT_NAME);
    }

    @Override
    public List<Restaurant> getAllForManager(int managerId) {
        return restaurantRepository.findAllByUserId(SORT_NAME, managerId);
    }
}
