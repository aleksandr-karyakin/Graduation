package com.company.project.web.ajax;

import com.company.project.AuthorizedUser;
import com.company.project.model.Restaurant;
import com.company.project.service.RestaurantService;
import com.company.project.service.VoteService;
import com.company.project.util.exception.TooLateVoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/ajax/restaurants")
public class UserUIController {

    private final RestaurantService restaurantService;
    private final VoteService voteService;

    private static final int VOTING_END_HOUR = 18;
    private static final String LOCATION_OF_RESTAURANTS = "Europe/Moscow";

    @Autowired
    public UserUIController(RestaurantService restaurantService, VoteService voteService) {
        this.restaurantService = restaurantService;
        this.voteService = voteService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllForUser();
    }

    @PostMapping(value = "/{restaurantId}/votes")
    public int vote(@PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        if (ZonedDateTime.now(ZoneId.of(LOCATION_OF_RESTAURANTS)).getHour() >= VOTING_END_HOUR) {
            throw new TooLateVoteException();
        }
        voteService.vote(authUser.getId(), restaurantId);
        return voteService.getCurrentRate(restaurantId);
    }

    @PostMapping(value = "/selected")
    public int getSelectedRestaurant(@AuthenticationPrincipal AuthorizedUser authUser) {
        return voteService.getSelectedRestaurant(authUser.getId());
    }
}
