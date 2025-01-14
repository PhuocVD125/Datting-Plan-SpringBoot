/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author pc
 */
@Getter
@Setter
public class PostUpdateDto {
    private Long recommendationId;
    private String title;
    private String content;
    private List<String> image;
    private List<String> tagTitle;
    
}
