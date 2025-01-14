/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto.request;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
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
public class ReplyRequestDto {
    private String content;
    private LocalDateTime time=LocalDateTime.now();
    private Long userId;
    private Long parentReplyUserId;
    
    private Long commentId;
}
