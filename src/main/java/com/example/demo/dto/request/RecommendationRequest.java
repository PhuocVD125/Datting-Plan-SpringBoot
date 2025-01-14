package com.example.demo.dto.request;

import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RecommendationRequest {

    private String location;     // Vị trí của recommendation
    private Double minBudget;          // Ngân sách tối thiểu
    private Double maxBudget;          // Ngân sách tối đa
    private String startTime;    // Thời gian bắt đầu (chuỗi dạng HH:mm)
    private String endTime;      // Thời gian kết thúc (chuỗi dạng HH:mm)
    private Set<Long> userTagIds; // Cac yeu cau cua user

}
