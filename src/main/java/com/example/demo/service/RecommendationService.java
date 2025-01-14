/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.RecommendationDto;
import com.example.demo.dto.RecommendationResponse;
import com.example.demo.dto.request.VoteRequest;
import com.example.demo.entity.Recommendation;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author pc
 */
public interface RecommendationService {
    /**
     * Lọc và gợi ý danh sách Recommendation phù hợp với nhu cầu người dùng.
     *
     * @param location     Địa điểm
     * @param startTime    Thời gian bắt đầu
     * @param endTime      Thời gian kết thúc
     * @param minBudget    Ngân sách tối thiểu
     * @param maxBudget    Ngân sách tối đa
     * @param userTagIds     Bộ tag sở thích của người dùng
     * @return Danh sách Recommendation sắp xếp theo độ phù hợp
     */
        List<Recommendation> filterRecommendations(
        String location,
        LocalTime startTime,
        LocalTime endTime,
        Double minBudget,
        Double maxBudget,
        Set<Long> userTagIds
);
    Recommendation getById(Long id);
    String createRecommendation(RecommendationDto rd);
    List<Recommendation> findByKeywordInLocationOrTitle(String keyword);
    Page<RecommendationResponse> getAllRecommendation(Pageable pageable);
    String addRating(Long recommendationId, VoteRequest ratingRequest);
//    Boolean hasUserRatedRecommendation(Long recommendationId, Long userId);
    Page<RecommendationResponse> searchRecommendations(String keyword,Pageable pageable);
    String toogleActiveRec(Long id);
    String updateRecommendation(Long id, RecommendationDto rd);

    String deleteRecommendation(Long id);
    Page<RecommendationResponse> getRecommendationByTag(String tagTitle,Pageable pageable);
}