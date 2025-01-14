package com.example.demo.dto.request;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BookingRequest {
    private Long userId;
    private Long recommendationId;
    private String bookingDate; 
    private List<ActivityRequest> activities; // Danh sách các hoạt động cần booking
}
