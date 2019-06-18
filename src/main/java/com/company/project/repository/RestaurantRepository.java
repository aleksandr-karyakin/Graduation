package com.company.project.repository;

import com.company.project.model.Restaurant;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {

    Restaurant save(Restaurant restaurant);

    @Transactional
    @Modifying
    @Query("DELETE FROM Restaurant r WHERE r.id=:id AND r.user.id=:managerId")
    int delete(@Param("id") int id, @Param("managerId") int managerId);

    Restaurant findByIdAndUserId(int id, int managerId);

    List<Restaurant> findAllByUserId(Sort sort, int managerId);

    @EntityGraph(attributePaths = {"dishes"})
    List<Restaurant> findAll(Sort sort);

    Restaurant getByName(String name);
}
