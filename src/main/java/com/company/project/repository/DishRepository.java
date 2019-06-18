package com.company.project.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.company.project.model.Dish;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Integer> {

    Dish save(Dish dish);

    @Transactional
    @Modifying
    @Query("DELETE FROM Dish d WHERE d.id=:id AND d.restaurant.id=:restaurantId")
    int delete(@Param("id") int id, @Param("restaurantId") int restaurantId);

    Dish findByIdAndRestaurantId(int id, int restaurantId);

    List<Dish> findAllByRestaurantId(Sort sort, int restaurantId);

    List<Dish> findAllByRestaurantIdAndEnabledTrue(Sort sort, int restaurantId);
}
