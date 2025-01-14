/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import com.example.demo.entity.Tag;
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
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {
    private Long recommendationId;
    private String title;
    private String content;
    private List<String> image;
    private List<String> tagTitle;
    private LocalDateTime time=LocalDateTime.now();
    private Long userId;
}
