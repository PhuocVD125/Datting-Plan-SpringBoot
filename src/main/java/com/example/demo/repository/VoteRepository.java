/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Recommendation;
import com.example.demo.entity.User;
import com.example.demo.entity.Vote;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author pc
 */

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vote v WHERE v.recommendation.id = :recommendationId AND v.userVote.id = :userId")
    boolean existsByRecommendationIdAndUserId(@Param("recommendationId") Long recommendationId, @Param("userId") Long userId);

    // Find a vote by recommendation and user
    @Query("SELECT v FROM Vote v WHERE v.recommendation.id = :recommendationId AND v.userVote.id = :userId")
    Optional<Vote> findByRecommendationIdAndUserId(@Param("recommendationId") Long recommendationId, @Param("userId") Long userId);
}
