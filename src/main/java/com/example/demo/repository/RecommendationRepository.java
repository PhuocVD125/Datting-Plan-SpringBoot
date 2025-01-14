/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Recommendation;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author pc
 */
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("SELECT r FROM Recommendation r "
            + "WHERE (r.location = :location) "
            + "AND (r.startTime IS NULL OR (r.startTime <= :startTime AND r.endTime >= :endTime)) "
            + "AND (:minBudget IS NULL OR :maxBudget IS NULL OR "
            + "      (r.minBudget >= :minBudget AND r.maxBudget <= :maxBudget)) "
            + "AND r.isActive = true")
    List<Recommendation> filterRecommendations(
            @Param("location") String location,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("minBudget") Double minBudget,
            @Param("maxBudget") Double maxBudget
    );

    @Query("SELECT r FROM Recommendation r "
            + "WHERE (r.location = :location) "
            + "AND (r.startTime IS NULL OR (r.startTime <= :startTime AND r.endTime >= :endTime)) "
            + "AND (:minBudget IS NULL OR :maxBudget IS NULL OR "
            + "      (r.minBudget >= :minBudget * 0.5 AND r.maxBudget <= :maxBudget * 1.5))"
            + "AND r.isActive = true")
    List<Recommendation> filterMoreRecommendations(
            @Param("location") String location,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("minBudget") Double minBudget,
            @Param("maxBudget") Double maxBudget
    );


    @Query("SELECT r FROM Recommendation r "
            + "WHERE LOWER(r.location) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Recommendation> findByKeywordInLocationOrTitle(@Param("keyword") String keyword, Pageable pageable);

//    @Query("SELECT r FROM Recommendation r WHERE r.location = :location AND r.isActive = :isAvailable AND (r.budget <= :maxPrice OR r.budget <= 0)")
//    Optional<List<Recommendation>> findByLocationAndIsAvailableAndMaxPriceOrZero(@Param("location") String location,
//                                                                             @Param("isAvailable") Boolean isAvailable,
//                                                                             @Param("maxPrice") Double maxPrice);
    @Query("SELECT r FROM Recommendation r WHERE LOWER(r.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recommendation> findByKeywordInLocationOrTitle(@Param("keyword") String keyword);
    @Query("SELECT r FROM Recommendation r "
            + "JOIN r.tags t "
            + "WHERE LOWER(t.title) = LOWER(:tagTitle)")
    Page<Recommendation> findByTagTitle(@Param("tagTitle") String tagTitle,Pageable pageable);
}
