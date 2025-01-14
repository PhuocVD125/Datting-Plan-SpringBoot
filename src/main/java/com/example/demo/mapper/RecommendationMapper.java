/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.mapper;

import com.example.demo.dto.RecommendationDto;
import com.example.demo.entity.Recommendation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author pc
 */
@Component
public class RecommendationMapper {

    private final ModelMapper modelMapper;

    public RecommendationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

    }

    public RecommendationDto convertToDto(Recommendation recomendation) {
        return modelMapper.map(recomendation, RecommendationDto.class);
    }

    public Recommendation convertToEntity(RecommendationDto recomendationDto) {
        return modelMapper.map(recomendationDto, Recommendation.class);
    }
}
