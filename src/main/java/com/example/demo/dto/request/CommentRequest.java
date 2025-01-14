/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author pc
 */
@AllArgsConstructor
@Getter
public class CommentRequest {
    private String content;
    private LocalDateTime time=LocalDateTime.now();
    private Long postId;
    private Long userId;
}
