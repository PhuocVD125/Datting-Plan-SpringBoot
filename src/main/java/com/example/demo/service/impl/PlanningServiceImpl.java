/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.PlanDetailDto;
import com.example.demo.dto.PlanningDetailDto;
import com.example.demo.dto.PlanningListDto;
import com.example.demo.dto.PlanningRecommendationDto;
import com.example.demo.dto.request.PlanDetailRequest;
import com.example.demo.dto.request.PlanningCreateRequest;
import com.example.demo.dto.request.PlanningRecommendationRequest;
import com.example.demo.dto.request.PlanningUpdateRequest;
import com.example.demo.entity.PlanDetail;
import com.example.demo.entity.Planning;
import com.example.demo.entity.PlanningRecommendation;
import com.example.demo.entity.Recommendation;
import com.example.demo.entity.User;
import com.example.demo.repository.PlanDetailRepository;
import com.example.demo.repository.PlanningRepository;
import com.example.demo.repository.RecommendationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.PlanningRecommendationRepository;
import com.example.demo.service.PlanningService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class PlanningServiceImpl implements PlanningService {

    private final PlanningRepository planningRepo;
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepo;
    private final PlanDetailRepository planningDetailRepo;
    private final PlanningRecommendationRepository planningRecommendationRepository;

    @Autowired
    public PlanningServiceImpl(PlanningRepository planningRepo, UserRepository userRepository, RecommendationRepository recommendationRepo, PlanDetailRepository planningDetailRepo, PlanningRecommendationRepository planningRecommendationRepository) {
        this.planningRepo = planningRepo;
        this.userRepository = userRepository;
        this.recommendationRepo = recommendationRepo;
        this.planningDetailRepo = planningDetailRepo;
        this.planningRecommendationRepository = planningRecommendationRepository;
    }

    @Override
    public String createPlan(PlanningCreateRequest pcr) {
        // Tạo đối tượng Planning mới
        Planning newPlan = new Planning();
        newPlan.setTitle(pcr.getTitle());
        newPlan.setTime(pcr.getTime());
        newPlan.setModifyAt(LocalDateTime.now());
        // Lấy thông tin người dùng từ cơ sở dữ liệu
        User user = userRepository.findById(pcr.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + pcr.getUserId()));
        newPlan.setUserPlanning(user);

        // Xử lý các chi tiết kế hoạch (PlanDetailRequest)
        List<PlanDetail> planDetails = new ArrayList<>();
        for (PlanDetailRequest detailRequest : pcr.getPlanDetails()) {
            // Tạo đối tượng PlanDetail
            PlanDetail planDetail = new PlanDetail();
            planDetail.setTitle(detailRequest.getTitle());
            planDetail.setPlan_condition(detailRequest.getPlan_condition());
            planDetail.setPlanning(newPlan); // Thiết lập mối quan hệ với Planning

            // Xử lý các recommendation trong PlanDetailRequest
            List<PlanningRecommendation> recommendations = new ArrayList<>();
            for (PlanningRecommendationRequest recRequest : detailRequest.getRecs()) {
                PlanningRecommendation planningRecommendation = new PlanningRecommendation();
                if(recRequest.getRecId()!=null){
                    Recommendation recommendationEntity= recommendationRepo.findById(recRequest.getRecId()).get();
                    planningRecommendation.setRecommendation(recommendationEntity);
                }
                     
                

                // Tạo đối tượng PlanningRecommendation
                
                
                planningRecommendation.setDescription(recRequest.getDescription());
                planningRecommendation.setStartTime(recRequest.getStartTime());
                planningRecommendation.setEndTime(recRequest.getEndTime());
                planningRecommendation.setIsBooked(false);
                planningRecommendation.setPlanning(planDetail); // Thiết lập mối quan hệ với PlanDetail

                recommendations.add(planningRecommendation);
            }

            // Gán danh sách recommendation vào PlanDetail
            planDetail.setPlanningRecommendations(recommendations);

            planDetails.add(planDetail);
        }

        // Gán danh sách PlanDetail vào Planning
        newPlan.setPlanDetails(planDetails);

        // Lưu đối tượng Planning vào cơ sở dữ liệu
        planningRepo.save(newPlan);

        return "Plan created successfully";
    }

    

    @Override
public String updatePlan(Long id, PlanningUpdateRequest pur) {
    // Tìm kế hoạch hiện tại
    Planning existingPlan = planningRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Planning not found with id: " + id));
    existingPlan.setTitle(pur.getTitle());
    for(PlanDetail pd:existingPlan.getPlanDetails())
        planningRecommendationRepository.deleteAllByPlanDetailId(pd.getId());
    // Xóa các PlanDetail cũ trước khi cập nhật
    planningDetailRepo.deleteAllByPlanningId(id);
    existingPlan.setTime(pur.getTime());
    
    // Kiểm tra và tạo danh sách PlanDetail và PlanningRecommendation mới
    List<PlanDetail> planDetails = new ArrayList<>();
    for (PlanDetailRequest detailRequest : pur.getPlanDetails()) {
        // Tạo đối tượng PlanDetail
        PlanDetail planDetail = new PlanDetail();
        planDetail.setTitle(detailRequest.getTitle());
        planDetail.setPlan_condition(detailRequest.getPlan_condition());
        planDetail.setPlanning(existingPlan); // Thiết lập mối quan hệ với Planning
        // Xử lý các PlanningRecommendation trong PlanDetailRequest
        List<PlanningRecommendation> recommendations = new ArrayList<>();
        for (PlanningRecommendationRequest recRequest : detailRequest.getRecs()) {
            PlanningRecommendation planningRecommendation = new PlanningRecommendation();
            if(recRequest.getRecId() != null){
                Recommendation recommendationEntity = recommendationRepo.findById(recRequest.getRecId())
                        .orElseThrow(() -> new IllegalArgumentException("Recommendation not found with id: " + recRequest.getRecId()));
                planningRecommendation.setRecommendation(recommendationEntity);
            }

            // Tạo đối tượng PlanningRecommendation
            planningRecommendation.setDescription(recRequest.getDescription());
            planningRecommendation.setStartTime(recRequest.getStartTime());
            planningRecommendation.setEndTime(recRequest.getEndTime());
            planningRecommendation.setIsBooked(false);
            planningRecommendation.setPlanning(planDetail); // Thiết lập mối quan hệ với PlanDetail

            recommendations.add(planningRecommendation);
        }

        // Gán danh sách recommendation vào PlanDetail
        planDetail.setPlanningRecommendations(recommendations);

        planDetails.add(planDetail);
    }

    // Cập nhật thời gian và danh sách PlanDetail mới vào Planning
    existingPlan.setModifyAt(LocalDateTime.now());
    existingPlan.setPlanDetails(planDetails); // Thêm các liên kết mới vào Planning

    // Lưu Planning sau khi đã cập nhật PlanDetails và PlanningRecommendations
    planningRepo.save(existingPlan);

    return "Plan updated successfully with ID: " + id;
}


    @Override
    public String deletePlan(Long id) {
        Planning existingPlan = planningRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Planning not found with id: " + id));
        planningRepo.delete(existingPlan);
        return "Plan deleted successfully with ID: " + existingPlan.getId();
    }

    @Override
    public PlanningDetailDto getById(Long id) {
    // Lấy đối tượng Planning từ cơ sở dữ liệu
    Planning planning = planningRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Planning not found with id: " + id));

    // Tạo đối tượng PlanningDetailDto và ánh xạ dữ liệu từ Planning
    PlanningDetailDto planningDetailDto = new PlanningDetailDto();
    planningDetailDto.setId(planning.getId());
    planningDetailDto.setUserId(planning.getUserPlanning().getId());
    planningDetailDto.setTitle(planning.getTitle());
    planningDetailDto.setTime(planning.getTime());
    planningDetailDto.setModifyAt(planning.getModifyAt());
    // Ánh xạ danh sách PlanDetail thành PlanDetailDto
    List<PlanDetailDto> planDetailDtos = planning.getPlanDetails().stream().map(planDetail -> {
        PlanDetailDto planDetailDto = new PlanDetailDto();
        planDetailDto.setId(planDetail.getId());
        planDetailDto.setTitle(planDetail.getTitle());
        planDetailDto.setPlanCondition(planDetail.getPlan_condition());
        Double budget = planDetail.getPlanningRecommendations().stream()
                .mapToDouble(rec -> rec.getRecommendation() != null ? 
                        rec.getRecommendation().getMaxBudget() : 0.0)
                .sum();
        planDetailDto.setBudget(budget);

        // Ánh xạ danh sách PlanningRecommendation thành PlanningRecommendationDto
        List<PlanningRecommendationDto> recommendationDtos = planDetail.getPlanningRecommendations().stream().map(rec -> {
            PlanningRecommendationDto recDto = new PlanningRecommendationDto();
            recDto.setId(rec.getId());
            recDto.setRecId(rec.getRecommendation() != null ? rec.getRecommendation().getId() : null);
            recDto.setTitle(rec.getRecommendation() != null ? rec.getRecommendation().getTitle() : "Your own thing you want to do");
            recDto.setLocation(rec.getRecommendation() != null ? rec.getRecommendation().getLocation() : null);
            recDto.setMinBudget(rec.getRecommendation() != null ? rec.getRecommendation().getMinBudget() : null);
            recDto.setAddress(rec.getRecommendation() != null ? rec.getRecommendation().getAddress(): null);
            recDto.setMaxBudget(rec.getRecommendation() != null ? rec.getRecommendation().getMaxBudget() : null);
            recDto.setDescription(rec.getDescription());
            recDto.setStartDate(rec.getRecommendation() != null ? rec.getRecommendation().getStartDate(): null);
            recDto.setEndDate(rec.getRecommendation() != null ? rec.getRecommendation().getEndDate(): null);
            recDto.setRecStartTime(rec.getRecommendation() != null ? rec.getRecommendation().getStartTime(): null);
            recDto.setRecEndTime(rec.getRecommendation() != null ? rec.getRecommendation().getEndTime(): null);
            recDto.setStartTime(rec.getStartTime());
            recDto.setEndTime(rec.getEndTime());
            recDto.setEmail(rec.getRecommendation() != null ? rec.getRecommendation().getEmail() : "None");
            recDto.setIsBooked(rec.getIsBooked());
            return recDto;
        }).collect(Collectors.toList());

        planDetailDto.setRecs(recommendationDtos);
        return planDetailDto;
    }).collect(Collectors.toList());

    planningDetailDto.setPlanDetailDtos(planDetailDtos);

    return planningDetailDto;
}


    @Override
    public Page<PlanningListDto> getByUser(Long userId, Pageable pageable) {
         
        Page<Planning> planningPage = planningRepo.findByUserId(userId, pageable);

        
        Page<PlanningListDto> planningListDtoPage = planningPage.map(planning -> {
            
            

            return new PlanningListDto(
                    planning.getId(),
                    planning.getTitle(),
                    planning.getTime(),
                    planning.getModifyAt()
            );
        });

        return planningListDtoPage;
    }

}
