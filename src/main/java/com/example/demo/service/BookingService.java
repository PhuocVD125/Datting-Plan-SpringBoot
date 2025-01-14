package com.example.demo.service;

import com.example.demo.dto.request.BookingRequest;
import com.example.demo.dto.request.CommentRequest;

public interface BookingService {
    String addBooking(BookingRequest bookingRequest);

}
