/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.request.CommentRequest;

/**
 *
 * @author pc
 */
public interface CommentService {
    String createComment(CommentRequest commentRequest);
    String deleteComment(Long id);
}
