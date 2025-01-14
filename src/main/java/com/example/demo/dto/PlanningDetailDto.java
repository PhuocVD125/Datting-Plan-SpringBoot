/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author pc
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class PlanningDetailDto {
    private Long id;
    private String title;
    private Long userId;
    private LocalDateTime time;
    private LocalDateTime modifyAt;    
    private List<PlanDetailDto> planDetailDtos;
}
