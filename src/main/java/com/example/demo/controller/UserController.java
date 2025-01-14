/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.config.FileSaveConfig;
import com.example.demo.dto.RecommendationDto;
import com.example.demo.dto.request.RegisterDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.request.LoginDto;
import com.example.demo.dto.request.PostCreateDto;
import com.example.demo.dto.request.UserUpdateDto;
import com.example.demo.entity.User;
import com.example.demo.service.LikeService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author pc
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final FileSaveConfig fileSaveConfig;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/add")
    public ResponseEntity<String> createUser(@RequestParam("rd") String rd, @RequestPart("image") MultipartFile file) {
        String response;
        ObjectMapper objectMapper = new ObjectMapper();
        RegisterDto rdto;
        try {
            rdto = objectMapper.readValue(rd, RegisterDto.class);
            System.out.println(fileSaveConfig.saveImage(file));
            rdto.setAvatar(fileSaveConfig.saveImage(file));
            response = userService.createUser(rdto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to process request", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping("/admin/add")
    public ResponseEntity<String> createAdminUser(@RequestParam("rd") String rd, @RequestPart("image") MultipartFile file) {
        String response;
        ObjectMapper objectMapper = new ObjectMapper();
        RegisterDto rdto;
        try {
            rdto = objectMapper.readValue(rd, RegisterDto.class);
            System.out.println(fileSaveConfig.saveImage(file));
            rdto.setAvatar(fileSaveConfig.saveImage(file));
            response = userService.createAdmin(rdto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to process request", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestParam("ud") String ud, @RequestPart("image") MultipartFile file, Model model) {
        String response;
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateDto updateDto;
        try {
            updateDto = objectMapper.readValue(ud, UserUpdateDto.class);
            updateDto.setImage(fileSaveConfig.saveImage(file));
            response = userService.updateUser(id, updateDto);
            if (response.equals("User Updated Succesfully")) {
                System.out.println(updateDto.getImage());
                model.addAttribute("userImage", updateDto.getImage());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to process request", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);

        SecurityContextHolder.clearContext();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto u = userService.findById(id);
        if (u != null) {
            return ResponseEntity.ok(u);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("get/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.findByUserName(username);
        return ResponseEntity.ok(userDto);
    }
    @GetMapping("/")
    public ResponseEntity<Page<UserDto>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullname") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(userService.getAllUser(pageable));
    }

    @PutMapping("/toggleActive/{id}")
    public ResponseEntity<String> toggleActiveUser(@PathVariable Long id) {
        String response = userService.toggleActive(id);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto, HttpSession session) {
//        UserDto u = userService.login(loginDto);  // Check the authorities granted to the user
//        // Optional: Fetch additional user info if needed
//        session.setAttribute("loggedInUser", u);
//        return new ResponseEntity<>(u, HttpStatus.OK);
//    }
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpSession session) {
//        try {
//            // Xác thực thông tin người dùng
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
//            );
//
//            // Lấy thông tin người dùng từ UserDetails
//            org.springframework.security.core.userdetails.User userDetails = 
//                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
//
//            // Lấy thông tin đầy đủ từ UserRepository
//            UserDto user = userService.findByUserName(userDetails.getUsername());
//                   
//
//            // Tạo UserDto
//            
//
//            // Lưu UserDto vào session
//            session.setAttribute("loggedInUser", user);
//            System.out.println(authentication.getPrincipal());
//            return ResponseEntity.ok(user);
//
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
//        }
//    }
}
