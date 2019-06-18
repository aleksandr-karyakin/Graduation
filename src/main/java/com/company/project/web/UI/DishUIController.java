package com.company.project.web.UI;

import com.company.project.AuthorizedUser;
import com.company.project.model.Dish;
import com.company.project.service.DishService;
import com.company.project.web.validator.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/manager/restaurants")
public class DishUIController {

    private final DishService service;

    @Autowired
    public DishUIController(DishService service) {
        this.service = service;
    }

    @PostMapping("/{restaurantId}/dishes")
    public void createOrUpdate(@Validated(View.Web.class) Dish dish, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        if (dish.isNew()) {
            service.checkAccess(restaurantId, restaurantId, authUser.getId());
            service.create(dish, restaurantId);
        } else {
            service.checkAccess(dish.getId(), restaurantId, authUser.getId());
            service.update(dish, restaurantId);
        }
    }

    @DeleteMapping("/{restaurantId}/dishes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(id, restaurantId, authUser.getId());
        service.delete(id, restaurantId);
    }

    @GetMapping(value = "/{restaurantId}/dishes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Dish get(@PathVariable int id, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(id, restaurantId, authUser.getId());
        return service.get(id, restaurantId);
    }

    @GetMapping(value = "/{restaurantId}/dishes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dish> getAll(@PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(restaurantId, restaurantId, authUser.getId());
        return service.getAllForManager(restaurantId);
    }


    @PostMapping("/{restaurantId}/dishes/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser, @RequestParam boolean enabled) {
        service.checkAccess(id, restaurantId, authUser.getId());
        service.enable(id, restaurantId, enabled);
    }
}
