/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.request.VoteRequest;
import com.example.demo.entity.Recommendation;
import com.example.demo.entity.User;
import com.example.demo.entity.Vote;
import com.example.demo.repository.RecommendationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class VoteServiceImpl implements VoteService{
    private final VoteRepository voteRepository ;
    private final UserRepository userRepository ;
    private final RecommendationRepository recommendationRepository ;
    @Autowired
    public VoteServiceImpl(VoteRepository voteRepository, UserRepository userRepository, RecommendationRepository recommendationRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.recommendationRepository = recommendationRepository;
    }
    

    @Override
    public boolean isRecVoteByUser(Long recId, Long userId) {
        return voteRepository.existsByRecommendationIdAndUserId(recId, userId);
    }

    @Override
    public String voteRec(VoteRequest vq) {
        User user = userRepository.findById(vq.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Recommendation recommendation=recommendationRepository.findById(vq.getRecId()).orElseThrow(() -> new RuntimeException("User not found"));
        if (voteRepository.existsByRecommendationIdAndUserId(recommendation.getId(), user.getId())) {
            // If already liked, remove the like (toggle feature)
            Vote vote = voteRepository.findByRecommendationIdAndUserId(recommendation.getId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("Like not found"));
            voteRepository.delete(vote);
            return "Post unliked successfully.";
        }

        // Otherwise, add a new like
        Vote vote = new Vote();
        vote.setRecommendation(recommendation);
        vote.setUserVote(user);
        voteRepository.save(vote);

        return "Post liked successfully.";
    }
    
}
