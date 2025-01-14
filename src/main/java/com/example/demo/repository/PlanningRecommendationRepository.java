/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.PlanningRecommendation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author pc
 */
public interface PlanningRecommendationRepository extends JpaRepository<PlanningRecommendation, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PlanningRecommendation pr WHERE pr.planning.id = :planDetailId")
    void deleteAllByPlanDetailId(@Param("planDetailId") Long planDetailId);
}
