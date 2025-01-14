/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import java.util.List;
import lombok.Getter;

/**
 *
 * @author pc
 */
@Getter
public class PlanDetailRequest {
    private String title;
    private String plan_condition;
    private List<PlanningRecommendationRequest> recs;
}
