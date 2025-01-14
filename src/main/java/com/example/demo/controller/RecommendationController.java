package com.example.demo.controller;

import com.example.demo.config.FileSaveConfig;
import com.example.demo.dto.PostDto;
import com.example.demo.dto.request.RecommendationRequest;
import com.example.demo.dto.RecommendationResponse;
import com.example.demo.dto.PreferenceTagDto;
import com.example.demo.dto.VoteDto;
import com.example.demo.dto.RecommendationDto;
import com.example.demo.dto.request.PostCreateDto;
import com.example.demo.dto.request.VoteRequest;
import com.example.demo.entity.Recommendation;
import com.example.demo.service.PostService;
import com.example.demo.service.RecommendationService;
import com.example.demo.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;
    private final FileSaveConfig fileSaveConfig;
    private final PostService postService;
    private final VoteService voteService;

    public RecommendationController(FileSaveConfig fileSaveConfig, PostService postService, VoteService voteService) {
        this.fileSaveConfig = fileSaveConfig;
        this.postService = postService;
        this.voteService = voteService;
    }
    
    
    /**
     * API để tìm kiếm gợi ý dựa trên yêu cầu của người dùng
     *
     * @param request Yêu cầu của người dùng chứa các thông tin về sở thích và
     * các bộ lọc
     * @return Danh sách các gợi ý được lọc theo yêu cầu của người dùng
     */
    @PostMapping("/filterRecommendations")
    public ResponseEntity<Page<RecommendationResponse>> filterRecommendations(
            @RequestBody RecommendationRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size);
        // Bước 2: Chuyển 'startTime' và 'endTime' từ chuỗi thành LocalTime
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        // Bước 3: Gọi service để lấy các gợi ý đã lọc theo yêu cầu của người dùng
        List<Recommendation> recommendationsList = recommendationService.filterRecommendations(
                request.getLocation(),
                startTime,
                endTime,
                request.getMinBudget(),
                request.getMaxBudget(),
                request.getUserTagIds()
        );

// Thực hiện phân trang trên danh sách
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), recommendationsList.size());
        List<Recommendation> pagedRecommendations = recommendationsList.subList(start, end);
        List<RecommendationResponse> responseList = pagedRecommendations.stream()
                .map(this::convertToRecommendationResponse)
                .collect(Collectors.toList());
        Page<RecommendationResponse> responsePage = new PageImpl<>(responseList, pageable, recommendationsList.size());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * Chuyển đổi một entity Recommendation thành RecommendationResponse DTO.
     *
     * @param recommendation Entity Recommendation cần chuyển đổi
     * @return RecommendationResponse DTO
     */
    private RecommendationResponse convertToRecommendationResponse(Recommendation recommendation) {
        // Chuyển đổi các tag thành danh sách PreferenceTagDto
        List<PreferenceTagDto> tags = recommendation.getTags().stream()
                .map(tag -> new PreferenceTagDto(tag.getId(), tag.getTitle()))
                .collect(Collectors.toList());

        // Chuyển đổi danh sách Rating thành RatingDto

        // Trả về DTO RecommendationResponse đã chuyển đổi
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

    // Son module
    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<String> createRecommendation(@RequestParam("rd") String rdJson,
            @RequestPart(value = "image", required = false) MultipartFile[] files) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RecommendationDto dto;
        try {
            List<String> urls = new ArrayList<>();
            // Parse JSON string to PostCreateDto object
            dto = objectMapper.readValue(rdJson, RecommendationDto.class);
            if(files!=null){
               for(MultipartFile mt:files)
                urls.add(fileSaveConfig.saveImage(mt));
               dto.setImage(urls); 
            }
            String response = recommendationService.createRecommendation(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Page<RecommendationResponse>> getAllRecommendation(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(recommendationService.getAllRecommendation(pageable));
    }

    @PostMapping("/{id}/ratings")
    public ResponseEntity<String> addRating(
            @PathVariable Long id,
            @RequestBody VoteRequest ratingRequest) {
        String response = recommendationService.addRating(id, ratingRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/vote")
    public ResponseEntity<String> voteRecommendation(@RequestBody VoteRequest vq) {
        String response = voteService.voteRec(vq);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{recId}/isVoted/{userId}")
    public ResponseEntity<Boolean> isRecVoted(@PathVariable Long recId, @PathVariable Long userId) {
        boolean isVoted = voteService.isRecVoteByUser(recId, userId);
        return ResponseEntity.ok(isVoted);
    }
    @GetMapping("/ptag/{title}")
    public ResponseEntity<Page<RecommendationResponse>> getRecByTag(
        @PathVariable String title, // Change to @PathVariable
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "6") int size,
        @RequestParam(defaultValue = "rating") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {

    Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return ResponseEntity.ok(recommendationService.getRecommendationByTag(title, pageable));
}
    @GetMapping("/search-pageable")
    public ResponseEntity<Page<RecommendationResponse>> searchRecommendationsPageable(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(recommendationService.searchRecommendations(keyword, pageable));
    }

    @GetMapping("/search")

    public List<RecommendationResponse> searchRecommendations(@RequestParam String keyword) {
        // Tìm danh sách Recommendation theo từ khóa
        List<Recommendation> recommendations = recommendationService.findByKeywordInLocationOrTitle(keyword);

        // Chuyển đổi danh sách Recommendation thành RecommendationResponse
        return recommendations.stream()
                .map(recommendation -> 
                        new RecommendationResponse(
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
                Long.valueOf(recommendation.getVotes().size()),
                recommendation.getMinBudget(),
                recommendation.getMaxBudget(),
                recommendation.getIsActive(),
                recommendation.getImage(),
                // Chuyển đổi danh sách tags sang PreferenceTagDto
                recommendation.getTags().stream()
                        .map(tag -> new PreferenceTagDto(tag.getId(), tag.getTitle()))
                        .collect(Collectors.toList())
                // Chuyển đổi danh sách ratings sang RatingDto

        ))
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecommendationResponse> getRecommendationById(@PathVariable Long id) {
        Recommendation response = recommendationService.getById(id);
        return ResponseEntity.ok(convertToRecommendationResponse(response));
    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<String> deleteRecommendation(@PathVariable Long id){
//        return ResponseEntity.ok(recommendationService.));
//    }


    /**
     * Cập nhật một Recommendation dựa trên ID.
     *
     * @param id ID của Recommendation cần cập nhật
     * @param rdJson Dữ liệu Recommendation cần cập nhật (dạng JSON)
     * @param files Hình ảnh đính kèm nếu có
     * @return ResponseEntity với kết quả trả về
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRecommendation(@PathVariable Long id,
                                                       @RequestParam("rd") String rdJson,
                                                       @RequestPart(value = "image", required = false) MultipartFile[] files,
                                                       @RequestPart(value = "urls", required = false) List<String> urls) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        RecommendationDto dto;

        try {
            // Parse JSON thành RecommendationDto
            dto = objectMapper.readValue(rdJson, RecommendationDto.class);

            // Lưu trữ các hình ảnh và lấy URL của chúng
            if (files != null && files.length > 0) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile file : files) {
                    imageUrls.add(fileSaveConfig.saveImage(file));  // Assume saveImage method stores file and returns the URL
                }
                dto.setImage(imageUrls);
            } else if (urls != null && !urls.isEmpty()) {
                dto.setImage(urls);  // Set the URLs directly if available
            } else {
                dto.setImage(new ArrayList<>());  // No new images provided, keep current or set empty list
            }

            // Gọi service để cập nhật Recommendation
            String response = recommendationService.updateRecommendation(id, dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Xóa một Recommendation dựa trên ID.
     *
     * @param id ID của Recommendation cần xóa
     * @return ResponseEntity với kết quả trả về
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecommendation(@PathVariable Long id) {
        try {
            // Gọi service để xóa Recommendation
            String response = recommendationService.deleteRecommendation(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/toggleRecActive/{id}")
    public ResponseEntity<String> toogleRecActive(@PathVariable Long id){
        return ResponseEntity.ok(recommendationService.toogleActiveRec(id));
    }
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("src/main/resources/static/images/" + filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // hoặc loại hình ảnh thích hợp
                        .body(resource);
            } else {
                throw new IOException("Image not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
