package com.example.demo.config;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {
    private final Map<Long, List<Long>> requestLogs = new ConcurrentHashMap<>();
    private final long TIME_WINDOW = 1 * 60 * 1000; //
    private final int MAX_REQUESTS = 3; // Chỉ cho phép 1 request trong tim window

    public boolean isAllowed(Long userId) {
        long currentTime = System.currentTimeMillis();
        requestLogs.putIfAbsent(userId, new ArrayList<>());

        List<Long> timestamps = requestLogs.get(userId);
        timestamps.removeIf(timestamp -> timestamp < currentTime - TIME_WINDOW);

        if (timestamps.size() < MAX_REQUESTS) {
            
            timestamps.add(currentTime);
            return true;
        }

        
        return false;
    }
}
