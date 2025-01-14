/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.request.LikeRequest;
import com.example.demo.entity.Like;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.LikeRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LikeService;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class LikeServiceImpl implements LikeService{
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String likePost(LikeRequest likeRequest) {
        // Fetch user and post entities
        User user = userRepository.findById(likeRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(likeRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if the user already liked the post
        if (likeRepository.existsByPostIdAndUserId(post.getId(), user.getId())) {
            // If already liked, remove the like (toggle feature)
            Like like = likeRepository.findByPostIdAndUserId(post.getId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("Like not found"));
            likeRepository.delete(like);
            return "Post unliked successfully.";
        }

        // Otherwise, add a new like
        Like like = new Like();
        like.setPostLike(post);
        like.setUser(user);
        likeRepository.save(like);

        return "Post liked successfully.";
    }

    @Override
    public boolean isPostLikedByUser(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
