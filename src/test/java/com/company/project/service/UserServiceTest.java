package com.company.project.service;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.ActiveDbProfileResolver;
import com.company.project.model.Role;
import com.company.project.model.User;
import com.company.project.util.exception.ErrorType;
import com.company.project.util.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static com.company.project.util.exception.NotFoundException.NOT_FOUND_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;
import static com.company.project.data.UserTestData.*;

@SpringJUnitConfig(locations = {"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Autowired
    private CacheManager cacheManager;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        cacheManager.getCache("users").clear();
        Session session = (Session) em.getDelegate();
        session.getSessionFactory().getCache().evictAllRegions();
    }

    @Test
    public void create() {
        User created = new User(null, "New", "new@gmail.com", "newPass", Role.ROLE_USER);
        service.create(created);
        assertMatch(service.getAll(), ADMIN, MANAGER, created, USER);
    }

    @Test
    public void update() {
        User updated = new User(USER);
        updated.setName("UpdatedName");
        service.update(updated);
        assertMatch(service.get(USER_ID), updated);
    }

    @Test
    public void updateNotFound() {
        User updated = new User(USER);
        updated.setId(1);
        NotFoundException e = assertThrows(NotFoundException.class, () -> service.update(updated));
        String msg = e.getMessage();
        assertTrue(msg.contains(ErrorType.DATA_NOT_FOUND.name()));
        assertTrue(msg.contains(NOT_FOUND_EXCEPTION));
        assertTrue(msg.contains(String.valueOf(1)));
    }

    @Test
    public void delete() {
        service.delete(USER_ID);
        assertMatch(service.getAll(), ADMIN, MANAGER);
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(1));
    }

    @Test
    public void get() {
        User user = service.get(USER_ID);
        assertMatch(user, USER);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(1));
    }

    @Test
    public void getByEmail() {
        User user = service.getByEmail("user@yandex.ru");
        assertMatch(user, USER);
    }

    @Test
    public void getAll() {
        List<User> all = service.getAll();
        assertMatch(all, ADMIN, MANAGER, USER);
    }

    @Test
    public void getAllManagers() {
        List<User> managers = service.getAllManagers();
        assertMatch(managers, ADMIN, MANAGER);
    }

    @Test
    void enable() {
        service.enable(USER_ID, false);
        assertFalse(service.get(USER_ID).isEnabled());
        service.enable(USER_ID, true);
        assertTrue(service.get(USER_ID).isEnabled());
    }
}