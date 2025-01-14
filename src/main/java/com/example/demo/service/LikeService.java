/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.request.LikeRequest;

/**
 *
 * @author pc
 */
public interface LikeService {
    String likePost(LikeRequest likeRequest);
    boolean isPostLikedByUser(Long postId, Long userId);
}
