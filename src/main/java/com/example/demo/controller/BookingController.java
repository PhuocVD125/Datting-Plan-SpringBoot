package com.example.demo.controller;

import com.example.demo.config.RateLimiter;
import com.example.demo.dto.request.BookingRequest;
import com.example.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    @Autowired
    private final BookingService bookService;

    public BookingController(BookingService bookService) {
        this.bookService = bookService;
    }
    

    @PostMapping
    public ResponseEntity<String> bookRecommend(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookService.addBooking(request));
    }
}
