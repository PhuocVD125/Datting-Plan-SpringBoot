/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.dto.PlanningDetailDto;
import com.example.demo.dto.PlanningListDto;
import com.example.demo.dto.request.PlanningCreateRequest;
import com.example.demo.dto.request.PlanningUpdateRequest;
import com.example.demo.entity.PlanDetail;
import com.example.demo.repository.PlanDetailRepository;
import com.example.demo.service.PlanningService;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 *
 * @author pc
 */
@RestController
@RequestMapping("/api/v1/planning")
public class PlanningController {

    @Autowired
    private PlanningService planningService;
    @PostMapping("/add")
    public ResponseEntity<String> createPlanning(@RequestBody PlanningCreateRequest pcr) {
        return ResponseEntity.ok(planningService.createPlan(pcr));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PlanningListDto>> getPlanningByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size,
            @RequestParam(defaultValue = "modifyAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PlanningListDto> planningDetailDtos = planningService.getByUser(userId, pageable);

        return ResponseEntity.ok(planningDetailDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanningDetailDto> getPlanningById(@PathVariable Long id) {
        PlanningDetailDto planningDetailDto = planningService.getById(id);
        return ResponseEntity.ok(planningDetailDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePlanning(@PathVariable Long id,@RequestBody PlanningUpdateRequest pur) {
        return ResponseEntity.ok(planningService.updatePlan(id, pur));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlanning(@PathVariable Long id) {
        return ResponseEntity.ok(planningService.deletePlan(id));
    }
}
