/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username).get();

        // Chuyển đổi Account thành UserDetails
        String role = u.getRole().toUpperCase();
        System.out.println("Loading user: " + username + " with role: " + role); // Log role

        UserDetails userDetails= new org.springframework.security.core.userdetails.User(
                u.getAccount().getUsername(),
                u.getAccount().getPassword(),
                List.of(new SimpleGrantedAuthority(role))
        );
        System.out.println(userDetails.getAuthorities());
        if(u.getIsActive())
            return userDetails;
        return null;
    }
}
