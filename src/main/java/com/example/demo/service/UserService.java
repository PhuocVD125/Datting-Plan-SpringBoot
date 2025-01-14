/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.request.RegisterDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.request.LoginDto;
import com.example.demo.dto.request.UserUpdateDto;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author pc
 */
public interface UserService {
    String createUser(RegisterDto userDto);
    String updateUser(Long id,UserUpdateDto userDto);
    UserDto findById(Long id);
    UserDto findByUserName(String userName);
    UserDto login(LoginDto ld);
    Page<UserDto> getAllUser(Pageable pageable);
    String toggleActive(Long id);
    String createAdmin(RegisterDto userDto);
    String deleteUser(Long id);
}
