package com.company.project.service;

import com.company.project.model.User;
import com.company.project.to.UserTo;

import java.util.List;

public interface UserService {

    User create(User user);

    void update(User user);

    void update(UserTo user);

    void delete(int id);

    User get(int id);

    User getByEmail(String email);

    List<User> getAll();

    List<User> getAllManagers();

    void enable(int id, boolean flag);

    void changeRoles(int id);
}
