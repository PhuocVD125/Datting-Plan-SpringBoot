/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author pc
 */
@AllArgsConstructor
@Getter
public class PlanningListDto {
    private Long id;
    private String title;
    private LocalDateTime time;
    private LocalDateTime modifyAt;
}
