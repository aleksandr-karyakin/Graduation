package com.company.project.web.validator;

import com.company.project.model.Restaurant;
import com.company.project.web.handler.ExceptionInfoHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import com.company.project.repository.RestaurantRepository;

@Component
public class UniqueNameValidator implements org.springframework.validation.Validator {

    private final RestaurantRepository repository;

    @Autowired
    public UniqueNameValidator(RestaurantRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return HasName.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        HasName restaurant = (HasName) target;
        Restaurant dbRestaurant = repository.getByName(restaurant.getName());
        if (dbRestaurant != null && !dbRestaurant.getId().equals(restaurant.getId())) {
            errors.rejectValue("name", ExceptionInfoHandler.EXCEPTION_DUPLICATE_NAME);
        }
    }
}
