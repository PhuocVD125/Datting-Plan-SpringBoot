/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller.web;

import com.example.demo.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author pc
 */
@ControllerAdvice
public class BaseController {

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", loggedInUser.getUsername());
            model.addAttribute("userImage", loggedInUser.getAvatar());
            if("ADMIN".equals(loggedInUser.getRole())){
                model.addAttribute("isADMIN", true);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }
    }
}
