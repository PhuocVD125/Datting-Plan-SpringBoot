/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author pc
 */
@Getter
public class PlanningUpdateRequest {
    private String title;
    private LocalDateTime time;
    private List<PlanDetailRequest> planDetails;
    
}
