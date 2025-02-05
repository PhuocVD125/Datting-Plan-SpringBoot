/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Like;
import com.example.demo.entity.User;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDto {   
    private Long id;
    private String recommendation;
    private String title;
    private String content;
    private String image;
    private LocalDateTime time;
    private Long userId;
    private String tags;
    private String username;
    private String userImage;
    private int likeCount;
    private int commentCount;
}
