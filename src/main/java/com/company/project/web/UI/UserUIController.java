package com.company.project.web.UI;

import com.company.project.AuthorizedUser;
import com.company.project.model.Restaurant;
import com.company.project.service.RestaurantService;
import com.company.project.service.VoteService;
import com.company.project.util.exception.TooLateVoteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/ajax/restaurants")
public class UserUIController {

    private final RestaurantService restaurantService;
    private final VoteService voteService;

    private static final LocalTime VOTING_END_TIME = LocalTime.of(18, 0);

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
        if (LocalTime.now().isAfter(VOTING_END_TIME)) {
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
