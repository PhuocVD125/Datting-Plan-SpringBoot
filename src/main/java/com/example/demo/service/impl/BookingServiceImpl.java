package com.example.demo.service.impl;

import com.example.demo.config.RateLimiter;
import com.example.demo.dto.request.ActivityRequest;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.entity.PlanningRecommendation;
import com.example.demo.entity.Recommendation;
import com.example.demo.entity.User;
import com.example.demo.repository.PlanningRecommendationRepository;
import com.example.demo.repository.RecommendationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final PlanningRecommendationRepository planningRecommendationRepository;
    @Autowired
    private final JavaMailSender mailSender;

    @Autowired
    private final RateLimiter rateLimiter;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, RecommendationRepository recommendationRepository, JavaMailSender mailSender, RateLimiter rateLimiter, PlanningRecommendationRepository planningRecommendationRepository) {
        this.userRepository = userRepository;
        this.recommendationRepository = recommendationRepository;
        this.mailSender = mailSender;
        this.rateLimiter = rateLimiter;
        this.planningRecommendationRepository = planningRecommendationRepository;
    }

    @Override
    public String addBooking(BookingRequest bookingRequest) {
        Long userId = bookingRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Recommendation re = recommendationRepository.findById(bookingRequest.getRecommendationId())
                .orElseThrow(() -> new RuntimeException("Recommendation not found"));
        // Kiểm tra giới hạn rate limit
        if (!rateLimiter.isAllowed(userId)) {
            return "You have exceeded the maximum number of booking requests. Please try again later.";
        }

        StringBuilder userEmailContent = new StringBuilder();
        StringBuilder recommendEmailContent = new StringBuilder();

        userEmailContent.append("Dear ").append(user.getAccount().getUsername())
                .append(" (").append(user.getFullname()).append("),\n\n")
                .append("You have successfully booked the following activities:\n\n");

        // Lặp qua danh sách các hoạt động
        for (ActivityRequest activity : bookingRequest.getActivities()) {
            PlanningRecommendation pr = planningRecommendationRepository.findById(activity.getPlanningRecId())
                    .orElseThrow(() -> new RuntimeException("Planning Recommendation not found"));


            // Đánh dấu hoạt động là đã được đặt
            pr.setIsBooked(Boolean.TRUE);
            planningRecommendationRepository.save(pr);

            // Thêm thông tin vào nội dung email
            userEmailContent.append("- Activity: ").append(re.getTitle()).append("\n")
                    .append("  Date: ").append(bookingRequest.getBookingDate()).append("\n")
                    .append("  Start Time: ").append(activity.getBookingStartTime()).append("\n")
                    .append("  End Time: ").append(activity.getBookingEndTime()).append("\n\n");

            recommendEmailContent.append("Dear Recommendation:'").append(re.getTitle()).append("'owner,\n\n")
                    .append("User ").append(user.getAccount().getUsername()).append(" (")
                    .append(user.getEmail()).append(", ").append(user.getPhoneNum()).append(")")
                    .append(" has requested a booking with you for:\n\n")
                    .append("- Activity: ").append(re.getTitle()).append("\n")
                    .append("  Date: ").append(bookingRequest.getBookingDate()).append("\n")
                    .append("  Start Time: ").append(activity.getBookingStartTime()).append("\n")
                    .append("  End Time: ").append(activity.getBookingEndTime()).append("\n\n");
        }

        // Gửi email cho user
        SimpleMailMessage userMessage = new SimpleMailMessage();
        userMessage.setTo(user.getEmail());
        userMessage.setSubject("Booking Confirmation");
        userMessage.setText(userEmailContent.toString());
        mailSender.send(userMessage);

        // Gửi email cho recommend owner
        SimpleMailMessage recommendMessage = new SimpleMailMessage();
        recommendMessage.setTo(re.getEmail());
        recommendMessage.setSubject("New Booking Request");
        recommendMessage.setText(recommendEmailContent.toString());
        mailSender.send(recommendMessage);

        return "Booking emails sent successfully!";
    }

}
