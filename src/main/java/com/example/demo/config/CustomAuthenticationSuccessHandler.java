/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.config;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

/**
 *
 * @author pc
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RequestCache requestCache = new HttpSessionRequestCache();
    public CustomAuthenticationSuccessHandler(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // Lấy thông tin từ Authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Lấy thông tin user từ database
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convert user entity sang DTO
        UserDto dto = userMapper.convertToDto(user);
        dto.setAvatar(user.getAvatar());
        dto.setUsername(user.getAccount().getUsername());
        // Lưu thông tin vào session
        HttpSession session = request.getSession();
        session.setAttribute("loggedInUser", dto);

        // Log thông tin để kiểm tra
        System.out.println("User saved to session: " + dto);

        // Chuyển hướng đến trang /home
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        String redirectUrl = "/home";
        if (savedRequest != null) {
            redirectUrl = savedRequest.getRedirectUrl(); // URL trước khi bị chặn
        }

        // Nếu không có URL trước đó, mặc định chuyển hướng về trang chính
        response.sendRedirect(redirectUrl);
    }
}
