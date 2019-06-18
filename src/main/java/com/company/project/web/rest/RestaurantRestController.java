package com.company.project.web.rest;

import com.company.project.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.company.project.AuthorizedUser;
import com.company.project.web.validator.View;
import com.company.project.service.RestaurantService;
import com.company.project.web.validator.UniqueNameValidator;

import java.net.URI;
import java.util.List;

import static com.company.project.util.ValidationUtil.assureIdConsistent;
import static com.company.project.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantRestController {

    static final String REST_URL = "/rest/manager/restaurants";

    private final RestaurantService service;

    private final UniqueNameValidator nameValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(nameValidator);
    }

    @Autowired
    public RestaurantRestController(RestaurantService service, UniqueNameValidator nameValidator) {
        this.service = service;
        this.nameValidator = nameValidator;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> createWithLocation(@Validated(View.Web.class) @RequestBody Restaurant restaurant, @AuthenticationPrincipal AuthorizedUser authUser) {
        checkNew(restaurant);
        Restaurant created = service.create(restaurant, authUser.getId());
        URI uriOfNewResource = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Restaurant restaurant, @PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        assureIdConsistent(restaurant, id);
        service.update(restaurant, authUser.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.delete(id, authUser.getId());
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable int id, @AuthenticationPrincipal AuthorizedUser authUser) {
        return service.get(id, authUser.getId());
    }

    @GetMapping
    public List<Restaurant> getAll(@AuthenticationPrincipal AuthorizedUser authUser) {
        return service.getAllForManager(authUser.getId());
    }
}
