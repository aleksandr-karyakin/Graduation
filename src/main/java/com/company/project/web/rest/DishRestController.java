package com.company.project.web.rest;

import com.company.project.model.Dish;
import com.company.project.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.company.project.AuthorizedUser;
import com.company.project.web.validator.View;

import java.net.URI;
import java.util.List;

import static com.company.project.util.ValidationUtil.assureIdConsistent;
import static com.company.project.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = DishRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DishRestController {

    static final String REST_URL = "/rest/manager/restaurants";

    private final DishService service;

    @Autowired
    public DishRestController(DishService service) {
        this.service = service;
    }

    @PostMapping(value = "/{restaurantId}/dishes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> createWithLocation(@Validated(View.Web.class) @RequestBody Dish dish, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(restaurantId, restaurantId, authUser.getId());
        checkNew(dish);
        Dish created = service.create(dish, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{restaurantId}/dishes/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Dish dish, @PathVariable int id, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(id, restaurantId, authUser.getId());
        assureIdConsistent(dish, id);
        service.update(dish, restaurantId);
    }

    @DeleteMapping("/{restaurantId}/dishes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(id, restaurantId, authUser.getId());
        service.delete(id, restaurantId);
    }

    @GetMapping("/{restaurantId}/dishes/{id}")
    public Dish get(@PathVariable int id, @PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(id, restaurantId, authUser.getId());
        return service.get(id, restaurantId);
    }

    @GetMapping("/{restaurantId}/dishes")
    public List<Dish> getAll(@PathVariable int restaurantId, @AuthenticationPrincipal AuthorizedUser authUser) {
        service.checkAccess(restaurantId, restaurantId, authUser.getId());
        return service.getAllForManager(restaurantId);
    }
}
