package com.company.project.web.rest;

import com.company.project.model.Dish;
import com.company.project.model.Restaurant;
import com.company.project.model.Vote;
import com.company.project.service.DishService;
import com.company.project.service.VoteService;
import com.company.project.util.exception.TooLateVoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.company.project.AuthorizedUser;
import com.company.project.service.RestaurantService;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = UserRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestController {

    static final String REST_URL = "/rest/restaurants";

    private final RestaurantService restaurantService;
    private final DishService dishService;
    private final VoteService voteService;

    private static final LocalTime VOTING_END_TIME = LocalTime.of(18, 0);

    @Autowired
    public UserRestController(RestaurantService restaurantService, DishService dishService, VoteService voteService) {
        this.restaurantService = restaurantService;
        this.dishService = dishService;
        this.voteService = voteService;
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllForUser();
    }

    @PostMapping(value = "/{restaurantId}/votes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Vote vote(@PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        if (LocalTime.now().isAfter(VOTING_END_TIME)) {
            throw new TooLateVoteException();
        }
        return voteService.vote(authUser.getId(), restaurantId);
    }

    @GetMapping(value = "/{restaurantId}/rating")
    public int getRating(@PathVariable int restaurantId) {
        return voteService.getCurrentRate(restaurantId);
    }

    @GetMapping(value = "/{restaurantId}/dishes")
    public List<Dish> getMenuByRestaurant(@PathVariable int restaurantId) {
        return dishService.getAllForUser(restaurantId);
    }
}
