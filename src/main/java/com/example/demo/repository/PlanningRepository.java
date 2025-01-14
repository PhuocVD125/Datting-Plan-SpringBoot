/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.Planning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author pc
 */
public interface PlanningRepository extends JpaRepository<Planning, Long>{
    @Query("SELECT p FROM Planning p WHERE p.userPlanning.id = :userId")
    Page<Planning> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
