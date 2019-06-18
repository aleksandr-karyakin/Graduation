package com.company.project.service;

import com.company.project.ActiveDbProfileResolver;
import com.company.project.model.Restaurant;
import com.company.project.util.exception.NotFoundException;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.company.project.data.RestaurantTestData.*;
import static com.company.project.data.UserTestData.MANAGER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(locations = {"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
public class RestaurantServiceTest {

    @Autowired
    private RestaurantService service;

    @Autowired
    private CacheManager cacheManager;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        cacheManager.getCache("restaurants").clear();
        Session session = (Session) em.getDelegate();
        session.getSessionFactory().getCache().evictAllRegions();
    }

    @Test
    @Transactional
    public void create() {
        Restaurant created = new Restaurant(null, "New Restaurant");
        service.create(created, MANAGER_ID);
        assertMatch(service.getAllForManager(MANAGER_ID), REST2, REST1, created);
        assertThat(created.getUser().getName()).isEqualTo("Manager");
    }

    @Test
    @Transactional
    public void update() {
        Restaurant newRest = new Restaurant(REST1_ID, "Updated Name");
        service.update(newRest, MANAGER_ID);
        assertMatch(service.get(REST1_ID, MANAGER_ID), newRest);
    }

    @Test
    @Transactional
    public void updateNotFound() {
        Restaurant newRest = new Restaurant(REST1_ID, "Updated Name");
        assertThrows(NotFoundException.class, () -> service.update(newRest, 1));
    }

    @Test
    @Transactional
    public void delete() {
        service.delete(REST1_ID, MANAGER_ID);
        assertMatch(service.getAllForUser(), REST2);
    }

    @Test
    @Transactional
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(REST1_ID, 1));
    }

    @Test
    void get() {
        Restaurant restaurant = service.get(REST1_ID, MANAGER_ID);
        assertMatch(restaurant, REST1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(REST1_ID, 1));
    }

    @Test
    void getAllForUser() {
        List<Restaurant> all = service.getAllForUser();
        assertMatch(all, REST2, REST1);
    }

    @Test
    void getAllForManager() {
        List<Restaurant> all = service.getAllForManager(MANAGER_ID);
        assertMatch(all, REST2, REST1);
    }
}