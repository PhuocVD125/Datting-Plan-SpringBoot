package com.example.demo.service.impl;

import com.example.demo.dto.*;
import com.example.demo.dto.request.VoteRequest;
import com.example.demo.entity.Recommendation;
import com.example.demo.mapper.RecommendationMapper;
import com.example.demo.repository.PreferenceTagRepository;
import com.example.demo.repository.RecommendationRepository;
import com.example.demo.service.RecommendationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import com.example.demo.entity.PreferenceTag;
import com.example.demo.entity.Vote;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.repository.VoteRepository;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final PreferenceTagRepository preferenceTagRepository;
    private final UserRepository userRepository;
    private final VoteRepository ratingRepository;

    public RecommendationServiceImpl(RecommendationMapper recommendationMapper, PreferenceTagRepository preferenceTagRepository, UserRepository userRepository, VoteRepository ratingRepository) {
        this.recommendationMapper = recommendationMapper;
        this.preferenceTagRepository = preferenceTagRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }

    /**
     * Lọc và gợi ý danh sách Recommendation phù hợp với nhu cầu người dùng.
     *
     * @param location Địa điểm cần tìm gợi ý.
     * @param startTime Thời gian bắt đầu mà gợi ý phải thỏa mãn.
     * @param endTime Thời gian kết thúc mà gợi ý phải thỏa mãn.
     * @param minBudget Ngân sách tối thiểu.
     * @param maxBudget Ngân sách tối đa.
     * @param userTagIds Sở thích của người dùng dưới dạng danh sách tag.
     * @return Danh sách Recommendation được sắp xếp theo độ phù hợp.
     */
    @Override
    public List<Recommendation> filterRecommendations(
            String location,
            LocalTime startTime,
            LocalTime endTime,
            Double minBudget,
            Double maxBudget,
            Set<Long> userTagIds
    ) {
        // Bước 1: Lọc cơ bản dựa trên yêu cầu của người dùng
        List<Recommendation> filteredRecommendations = recommendationRepository.filterRecommendations(
                location, startTime, endTime, minBudget, maxBudget
        );
        LocalTime expandedStartTime = expandTime(startTime, -0.2);  // Mở rộng thời gian bắt đầu giảm 20%
        LocalTime expandedEndTime = expandTime(endTime, 0.2);  // Mở rộng thời gian bắt đầu tăng 20%

        // Bước 2: Lọc mở rộng với các khoảng thời gian và ngân sách mở rộng
        List<Recommendation> extendedRecommendations = recommendationRepository.filterMoreRecommendations(
                location, expandedStartTime,expandedEndTime, minBudget, maxBudget
        );
        // Bước 3: Tính toán độ phù hợp cho mỗi recommendation
        List<Recommendation> allRecommendationsSet = Stream.concat(filteredRecommendations.stream(), extendedRecommendations.stream())
                .distinct() // Loại bỏ trùng lặp trước khi tính toán
                .map(r -> new AbstractMap.SimpleEntry<>(r, calculateRelevance(userTagIds, extractTagIds(r)))) // Tính toán độ phù hợp
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // Sắp xếp theo độ phù hợp giảm dần
                .map(Map.Entry::getKey) // Chỉ lấy các recommendation
                .collect(Collectors.toList()); // Duy trì thứ tự kết quả


        // Bước 4: Chuyển Set thành List để phân trang và đảm bảo sắp xếp
       return allRecommendationsSet;
    }

    /**
     * Trích xuất danh sách tag từ một Recommendation.
     *
     * @param recommendation Gợi ý cần trích xuất tag.
     * @return Tập hợp các tag của gợi ý.
     */
    private Set<Long> extractTagIds(Recommendation recommendation) {
        if (recommendation.getTags() == null) {
            return Collections.emptySet();
        }
        return recommendation.getTags().stream()
                .map(PreferenceTag::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Tính toán độ phù hợp giữa ID tag của người dùng và ID tag của
     * Recommendation.
     *
     * @param userTagIds Danh sách ID tag từ yêu cầu người dùng.
     * @param recommendationTagIds Danh sách ID tag của Recommendation.
     * @return Giá trị độ phù hợp (giá trị từ 0 đến 1).
     */
    private double calculateRelevance(Set<Long> userTagIds, Set<Long> recommendationTagIds) {
        // Kết hợp tất cả các tag để tạo không gian vector chung
        Set<Long> combinedTagIds = new HashSet<>(userTagIds);
        combinedTagIds.addAll(recommendationTagIds);

        // Tạo vector boolean cho người dùng và Recommendation
        int[] userVector = combinedTagIds.stream().mapToInt(tag -> userTagIds.contains(tag) ? 1 : 0).toArray();
        int[] recommendationVector = combinedTagIds.stream().mapToInt(tag -> recommendationTagIds.contains(tag) ? 1 : 0).toArray();

        // Tính cosine similarity
        double dotProduct = 0.0, userMagnitude = 0.0, recommendationMagnitude = 0.0;
        for (int i = 0; i < userVector.length; i++) {
            dotProduct += userVector[i] * recommendationVector[i];
            userMagnitude += userVector[i] * userVector[i];
            recommendationMagnitude += recommendationVector[i] * recommendationVector[i];
        }

        // Trả về giá trị cosine similarity
        return (userMagnitude == 0 || recommendationMagnitude == 0)
                ? 0
                : dotProduct / (Math.sqrt(userMagnitude) * Math.sqrt(recommendationMagnitude));
    }

    private LocalTime expandTime(LocalTime originalTime, double factor) {
        if (originalTime == null) {
            return null;
        }

        // Chuyển đổi thời gian sang giây để có thể tính toán dễ dàng
        long totalSeconds = originalTime.toSecondOfDay();
        long expandedSeconds = (long) (totalSeconds * (1 + factor)); // Tính toán thời gian mở rộng

        // Đảm bảo giá trị không vượt quá 86400 giây (tương ứng với cuối ngày)
        expandedSeconds = Math.min(86400, Math.max(0, expandedSeconds)); // Giới hạn từ 0 đến 86400

        // Nếu giá trị là 86400, trả về thời điểm cuối ngày (23:59:59)
        return expandedSeconds == 86400 ? LocalTime.MAX : LocalTime.ofSecondOfDay(expandedSeconds);
    }

    @Override
    public String createRecommendation(RecommendationDto rd) {
        Recommendation newRecommendation = toEntity(rd);
        newRecommendation.setIsActive(Boolean.TRUE);
        Set<PreferenceTag> preferenceTags = new HashSet<>();
        for (Long i : rd.getListPreferenceTagId()) {
            preferenceTags.add(preferenceTagRepository.findById(i).get());
        }
        newRecommendation.setTags(preferenceTags);
        recommendationRepository.save(newRecommendation);
        return "New Recommendation Created";
    }

    @Override
    public List<Recommendation> findByKeywordInLocationOrTitle(String keyword) {
        List<Recommendation> recommendations = recommendationRepository.findByKeywordInLocationOrTitle(keyword);
        return recommendations;
    }

    public static Recommendation toEntity(RecommendationDto dto) {
        Recommendation recommendation = new Recommendation();
        recommendation.setLocation(dto.getLocation());
        recommendation.setEmail(dto.getEmail());
        recommendation.setTitle(dto.getTitle());
        recommendation.setStartTime(dto.getStartTime());
        recommendation.setStartDate(dto.getStartDate());
        recommendation.setEndDate(dto.getEndDate());
        recommendation.setEndTime(dto.getEndTime());
        recommendation.setRecommendTime(dto.getRecommendTime());
        recommendation.setDescription(dto.getDescription());
        recommendation.setRating(dto.getRating());
        recommendation.setMinBudget(dto.getMinBudget());
        recommendation.setMaxBudget(dto.getMaxBudget());
        recommendation.setIsActive(dto.getIsActive());
        recommendation.setImage(dto.getImage());
        recommendation.setAddress(dto.getAddress());
        return recommendation;
    }

    @Override
    public Recommendation getById(Long id) {
        return recommendationRepository.findById(id).get();
    }

    @Override
    public Page<RecommendationResponse> getAllRecommendation(Pageable pageable) {
        return recommendationRepository.findAll(pageable).map(this::convertToRecommendationResponse);
    }

    private RecommendationResponse convertToRecommendationResponse(Recommendation recommendation) {
        // Chuyển đổi các tag thành danh sách PreferenceTagDto
        List<PreferenceTagDto> tags = recommendation.getTags().stream()
                .map(tag -> new PreferenceTagDto(tag.getId(), tag.getTitle()))
                .collect(Collectors.toList());
        return new RecommendationResponse(
                recommendation.getId(),
                recommendation.getLocation(),
                recommendation.getEmail(),
                recommendation.getTitle(),
                recommendation.getAddress(),
                recommendation.getStartTime(),
                recommendation.getEndTime(),
                recommendation.getStartDate(),
                recommendation.getEndDate(),
                recommendation.getRecommendTime(),
                recommendation.getDescription(),
                Long .valueOf(recommendation.getVotes().size()),
                recommendation.getMinBudget(),
                recommendation.getMaxBudget(),
                recommendation.getIsActive(),
                recommendation.getImage(),
                tags
        );
    }

    @Override
    public String addRating(Long recommendationId, VoteRequest ratingRequest) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));

        User u = userRepository.findById(ratingRequest.getUserId()).get();

        // Lấy danh sách rating hiện tại
        List<Vote> votes = recommendation.getVotes();

        // Cộng giá trị rating mới vào danh sách
        Vote vote=  new Vote();
        vote.setUserVote(u);
        vote.setTime(LocalDateTime.now());
        votes.add(vote);

        // Tính toán rating trung bình mới
        

        // Cập nhật rating trung bình trong Recommendation
        recommendation.setRating(Long.valueOf(votes.size()));

        // Lưu Recommendation vào database
        try {
            recommendationRepository.save(recommendation);
        } catch (Exception e) {
            return "You have already rated thís recommendation";
        }

        // Chuyển Recommendation thành RecommendationResponse
        // Chuyển Recommendation sang RecommendationResponse
        return "Add rating ok";
    }

//    @Override
//    public Boolean hasUserRatedRecommendation(Long recommendationId, Long userId) {
//        return ratingRepository.existsByRecommendationAndUser(recommendationId, userId);
//    }
    @Override
    public Page<RecommendationResponse> searchRecommendations(String keyword, Pageable pageable) {
        return recommendationRepository.findByKeywordInLocationOrTitle(keyword, pageable).map(this::convertToRecommendationResponse);
    }

    // update, delete
    @Override
    public String updateRecommendation(Long id, RecommendationDto rd) {
        // Find existing recommendation
        Recommendation existingRecommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));

        // Update the fields of the recommendation
        existingRecommendation.setLocation(rd.getLocation());
        existingRecommendation.setEmail(rd.getEmail());
        existingRecommendation.setTitle(rd.getTitle());
        existingRecommendation.setStartTime(rd.getStartTime());
        existingRecommendation.setEndTime(rd.getEndTime());
        existingRecommendation.setStartDate(rd.getStartDate());
        existingRecommendation.setRecommendTime(rd.getRecommendTime());
        existingRecommendation.setEndDate(rd.getEndDate());
        existingRecommendation.setDescription(rd.getDescription());
        existingRecommendation.setRating(rd.getRating());
        existingRecommendation.setMinBudget(rd.getMinBudget());
        existingRecommendation.setMaxBudget(rd.getMaxBudget());
        existingRecommendation.setIsActive(rd.getIsActive());
        existingRecommendation.setImage(rd.getImage());
        existingRecommendation.setAddress(rd.getAddress());
        // Update tags
        Set<PreferenceTag> preferenceTags = new HashSet<>();
        for (Long i : rd.getListPreferenceTagId()) {
            preferenceTags.add(preferenceTagRepository.findById(i).orElseThrow(() -> new EntityNotFoundException("Tag not found")));
        }
        existingRecommendation.setTags(preferenceTags);

        // Save the updated recommendation
        recommendationRepository.save(existingRecommendation);
        return "Recommendation updated successfully";
    }

    @Override
    public String deleteRecommendation(Long id) {
        // Check if the recommendation exists
        Recommendation existingRecommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation not found"));

        // Delete the recommendation
        recommendationRepository.delete(existingRecommendation);
        return "Recommendation deleted successfully";
    }

    @Override
    public Page<RecommendationResponse> getRecommendationByTag(String tagTitle,Pageable pageable) {
        return recommendationRepository.findByTagTitle(tagTitle, pageable).map(this::convertToRecommendationResponse);
    }

    @Override
    public String toogleActiveRec(Long id) {
        Recommendation r=recommendationRepository.findById(id).get();
        r.setIsActive(!r.getIsActive());
        recommendationRepository.save(r);
        return r.getIsActive() ? "Rec activated" : "Rec deactivated";
    }

    

}
