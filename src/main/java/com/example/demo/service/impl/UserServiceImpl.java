/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.request.RegisterDto;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.request.LoginDto;
import com.example.demo.dto.request.UserUpdateDto;
import com.example.demo.entity.Account;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author pc
 */
@Service

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public String createUser(RegisterDto registerDto) {
        if (accountRepository.existsByUsername(registerDto.getUsername()))
            return "Username already exsisted";
        User newUser = new User();
        newUser.setFullname(registerDto.getFullname());
        newUser.setIsActive(Boolean.TRUE);
        newUser.setGender(registerDto.getGender());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPhoneNum(registerDto.getPhoneNum());
        newUser.setAvatar(registerDto.getAvatar());
        newUser.setRole("USER");
        Account a = new Account(registerDto.getUsername(), passwordEncoder.encode(registerDto.getPassword()));
        newUser.setAccount(a);
        System.out.println(newUser.getId());
        userRepository.save(newUser);
        return "New User Created";
    }


    @Override
    public String updateUser(Long id, UserUpdateDto userDto) {
        Optional<User> u = userRepository.findById(id);
        if (u.isEmpty()) {
            return "Cant find User";
        }
        u.get().setFullname(userDto.getFullname());
        u.get().setAvatar(userDto.getImage());
        u.get().setEmail(userDto.getEmail());
        u.get().setPhoneNum(userDto.getPhoneNum());
        userRepository.save(u.get());
        return "User Updated Succesfully";
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> u = userRepository.findById(id);
        UserDto user = userMapper.convertToDto(u.get());
        user.setUsername(u.get().getAccount().getUsername());
        return user;
    }

    @Override
    public UserDto findByUserName(String userName) {
        User user = userRepository.findByUsername(userName).get();
        UserDto u = userMapper.convertToDto(user);
        u.setUsername(user.getAccount().getUsername());
        return u;
    }

    @Override
    public UserDto login(LoginDto ld) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(ld.getUsername());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            String encodedPassword = account.getPassword();
            System.out.println(encodedPassword);
            if (passwordEncoder.matches(ld.getPassword(), encodedPassword)) {
                User user = userRepository.findByUsername(ld.getUsername()).get();
                UserDto u = userMapper.convertToDto(user);
                u.setUsername(user.getAccount().getUsername());
                if (user.getIsActive())
                    return u;
            } else System.out.println("wrong password");
        } else System.out.println("not found");
        return null;
    }

    @Override
    public String toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(!user.getIsActive()); // Đổi trạng thái isActive
        userRepository.save(user); // Lưu lại thay đổi

        return user.getIsActive() ? "User activated" : "User deactivated";
    }
    public UserDto convertToUserDto(User u){
        UserDto user = userMapper.convertToDto(u);
        user.setUsername(u.getAccount().getUsername());
        return user;
    }
    @Override
    public Page<UserDto> getAllUser(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToUserDto);
    }

    @Override
    public String createAdmin(RegisterDto registerDto) {
        if (accountRepository.existsByUsername(registerDto.getUsername()))
            return "Username already exsisted";
        User newUser = new User();
        newUser.setFullname(registerDto.getFullname());
        newUser.setIsActive(Boolean.TRUE);
        newUser.setGender(registerDto.getGender());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPhoneNum(registerDto.getPhoneNum());
        newUser.setAvatar(registerDto.getAvatar());
        newUser.setRole("ADMIN");
        Account a = new Account(registerDto.getUsername(), passwordEncoder.encode(registerDto.getPassword()));
        newUser.setAccount(a);
        System.out.println(newUser.getId());
        userRepository.save(newUser);
        return "New User Created";
    }

    @Override
    public String deleteUser(Long id) {
        User u=userRepository.findById(id).get();
        userRepository.delete(u);
        return "Deleted User Successfully";
    }
}
