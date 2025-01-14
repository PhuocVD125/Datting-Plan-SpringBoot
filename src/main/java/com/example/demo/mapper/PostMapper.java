/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.mapper;

import com.example.demo.dto.request.PostCreateDto;
import com.example.demo.entity.Post;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author pc
 */
@Component
public class PostMapper {
    private final ModelMapper modelMapper;

    public PostMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    public PostCreateDto convertToDto(Post post) {
        return modelMapper.map(post, PostCreateDto.class);
    }

    public Post convertToEntity(PostCreateDto postDto) {
        return modelMapper.map(postDto, Post.class);
    }
}
