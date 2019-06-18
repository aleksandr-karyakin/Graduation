package com.company.project.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.model.Role;
import com.company.project.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User save(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id=:id")
    int delete(@Param("id") int id);

    @EntityGraph(attributePaths = {"roles"})
    User findUserById(int id);

    User findByEmail(String email);

    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll(Sort sort);

    List<User> findAllByRoles(Role role, Sort sort);
}
