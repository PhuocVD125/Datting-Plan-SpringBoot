/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import jakarta.persistence.Entity;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author pc
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlanningRecommendationRequest {
    private Long recId;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
}
