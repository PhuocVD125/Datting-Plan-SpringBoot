/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author pc
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanningRecommendationDto {
    private Long id;
    private Long recId;
    private String email;
    private String title;
    private String location;
    private String address;
    private Double minBudget;
    private Double maxBudget;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalTime recStartTime;
    private LocalTime recEndTime;
    private Boolean isBooked;
}
