package com.company.project.web.ajax;

import com.company.project.AuthorizedUser;
import com.company.project.model.Restaurant;
import com.company.project.service.RestaurantService;
import com.company.project.web.validator.UniqueNameValidator;
import com.company.project.web.validator.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ajax/manager/restaurants")
public class RestaurantsUIController {

    private final RestaurantService service;

    private final UniqueNameValidator nameValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(nameValidator);
    }

    @Autowired
    public RestaurantsUIController(RestaurantService service, UniqueNameValidator nameValidator) {
        this.service = service;
        this.nameValidator = nameValidator;
    }

    @PostMapping
    public void createOrUpdate(@Validated(View.Web.class) Restaurant restaurant, @AuthenticationPrincipal AuthorizedUser authUser) {
        if (restaurant.isNew()) {
            service.create(restaurant, authUser.getId());
        } else {
            service.update(restaurant, authUser.getId());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.delete(id, authUser.getId());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Restaurant get(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        return service.get(id, authUser.getId());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Restaurant> getAll(@AuthenticationPrincipal AuthorizedUser authUser) {
        return service.getAllForManager(authUser.getId());
    }
}
