/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author pc
 */
@AllArgsConstructor
@Getter
@Setter
public class RecommendationResponse {
    private Long id;
    private String location;
    private String email;
    private String title;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String recommendTime;
    private String description;
    private Long rating;
    private Double minBudget;
    private Double maxBudget;
    private Boolean isActive;
    private List<String> image;
    private List<PreferenceTagDto> tags;
}
