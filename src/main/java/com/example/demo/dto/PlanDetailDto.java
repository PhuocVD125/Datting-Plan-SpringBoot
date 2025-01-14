/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author pc
 */
@Setter
@Getter
public class PlanDetailDto {
    private Long id;
    private String planCondition;
    private String title;
    private List<PlanningRecommendationDto> recs;
    private Double budget;
}
