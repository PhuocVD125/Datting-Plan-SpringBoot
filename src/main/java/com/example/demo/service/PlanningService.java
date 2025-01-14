/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.PlanningDetailDto;
import com.example.demo.dto.PlanningListDto;
import com.example.demo.dto.request.PlanningCreateRequest;
import com.example.demo.dto.request.PlanningUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author pc
 */
public interface PlanningService {
    String createPlan(PlanningCreateRequest pcr);
    String updatePlan(Long id,PlanningUpdateRequest pur);
    String deletePlan(Long id);
    PlanningDetailDto getById(Long id);
    Page<PlanningListDto> getByUser(Long userId,Pageable pageable);
}
