package com.company.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.company.project.ActiveDbProfileResolver;
import com.company.project.service.DishService;
import com.company.project.service.RestaurantService;
import com.company.project.service.UserService;

import javax.validation.ConstraintViolationException;
import java.util.EnumSet;

import static org.assertj.core.util.Throwables.getRootCause;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.company.project.data.RestaurantTestData.REST1_ID;
import static com.company.project.data.UserTestData.MANAGER_ID;

@SpringJUnitConfig(locations = {"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@Sql(scripts = "classpath:db/hsqldb/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
class ValidatorTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private DishService dishService;

    @Test
    void userValidation() {
        validateRootCause(() -> userService.create(new User(null, "  ", "mail@yandex.ru", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        validateRootCause(() -> userService.create(new User(null, "a", "mail@yandex.ru", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        validateRootCause(() -> userService.create(new User(null, "User", "  ", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        validateRootCause(() -> userService.create(new User(null, "User", "mail", "password", Role.ROLE_USER)), ConstraintViolationException.class);
        validateRootCause(() -> userService.create(new User(null, "User", "mail@yandex.ru", "password", true, null, EnumSet.of(Role.ROLE_USER))), ConstraintViolationException.class);
    }

    @Test
    void restaurantValidation() {
        validateRootCause(() -> restaurantService.create(new Restaurant(null, " "), MANAGER_ID), ConstraintViolationException.class);
        validateRootCause(() -> restaurantService.create(new Restaurant(null, "a"), MANAGER_ID), ConstraintViolationException.class);
    }

    @Test
    void dishValidation() {
        validateRootCause(() -> dishService.create(new Dish(null, " ", 100, true), REST1_ID), ConstraintViolationException.class);
        validateRootCause(() -> dishService.create(new Dish(null, "a", 100, true), REST1_ID), ConstraintViolationException.class);
        validateRootCause(() -> dishService.create(new Dish(null, "dish", 0, true), REST1_ID), ConstraintViolationException.class);
        validateRootCause(() -> dishService.create(new Dish(null, "dish", 10010, true), REST1_ID), ConstraintViolationException.class);
        validateRootCause(() -> dishService.create(new Dish(null, "dish", 10010, true, null), REST1_ID), ConstraintViolationException.class);
    }

    static <T extends Throwable> void validateRootCause(Runnable runnable, Class<T> exceptionClass) {
        assertThrows(exceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }
}
