package com.company.project.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.ActiveDbProfileResolver;
import com.company.project.model.Dish;
import com.company.project.util.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static com.company.project.data.DishTestData.*;
import static com.company.project.data.RestaurantTestData.REST1_ID;

@SpringJUnitConfig(locations = {"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class DishServiceTest {

    @Autowired
    private DishService service;

    @Test
    @Transactional
    public void create() {
        assertMatch(service.getAllForManager(REST1_ID), DISH1, DISH2);
        assertMatch(service.getAllForUser(REST1_ID), DISH1);
        Dish created = new Dish(null, "steak", 200, false);
        service.create(created, REST1_ID);

        assertMatch(service.getAllForManager(REST1_ID), DISH1, DISH2, created);
        assertMatch(service.getAllForUser(REST1_ID), DISH1);
        created.setEnabled(true);
        assertMatch(service.getAllForUser(REST1_ID), DISH1, created);

        assertThat(created.getRestaurant().getName()).isEqualTo("McDonalds");
        assertThat(created.getRestaurant().getUser().getName()).isEqualTo("Manager");
    }

    @Test
    @Transactional
    public void update() {
        Dish updated = new Dish(DISH1_ID, "UpdatedName", 200, true);
        service.update(updated, REST1_ID);
        assertMatch(service.get(DISH1_ID, REST1_ID), updated);
    }

    @Test
    @Transactional
    public void updateNotFound() {
        Dish updated = new Dish(DISH1_ID, "UpdatedName", 200, true);
        assertThrows(NotFoundException.class, () -> service.update(updated, 1));
    }

    @Test
    @Transactional
    public void delete() {
        assertMatch(service.getAllForManager(REST1_ID), DISH1, DISH2);
        service.delete(DISH1_ID, REST1_ID);
        assertMatch(service.getAllForManager(REST1_ID), DISH2);
    }

    @Test
    @Transactional
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(DISH1_ID, 1));
    }

    @Test
    void get() {
        Dish dish = service.get(DISH1_ID, REST1_ID);
        assertMatch(dish, DISH1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(DISH1_ID, 1));
    }

    @Test
    void getAllForUser() {
        List<Dish> all = service.getAllForUser(REST1_ID);
        assertMatch(all, DISH1);
    }

    @Test
    void getAllForManager() {
        List<Dish> all = service.getAllForManager(REST1_ID);
        assertMatch(all, DISH1, DISH2);
    }

    @Test
    void enable() {
        assertTrue(service.get(DISH1_ID, REST1_ID).isEnabled());
        service.enable(DISH1_ID, REST1_ID, false);
        assertFalse(service.get(DISH1_ID, REST1_ID).isEnabled());
        service.enable(DISH1_ID, REST1_ID, true);
        assertTrue(service.get(DISH1_ID, REST1_ID).isEnabled());
    }
}