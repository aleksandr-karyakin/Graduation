package com.company.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.company.project.model.Vote;

import java.time.LocalDate;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Vote save(Vote vote);

    Vote findByUserIdAndDate(int userId, LocalDate date);

    Integer countByRestaurantIdAndDate(int restaurantId, LocalDate date);
}
