/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.dto;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ReplyDto {

    private Long id;
    private String content;
    private String commentReply;
    private LocalDateTime time;
    private Long userId; // ID of the user who created the reply
    private String username; // Username of the user
    private String userImage;
    private Long parentUserId;
    private String parentUsername; // Username of the parent comment (optional for replies)
    private List<ReplyDto> childReplies = new ArrayList<>(); // Nested replies
}
