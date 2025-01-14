/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller.web;

import com.example.demo.dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author pc
 */
@Controller
public class UserWebController {

    @GetMapping("/home")
    public String index(Model model) {
        model.addAttribute("message", "Hello, Thymeleaf with Tailwind CSS!");
        return "home";
    }
    @GetMapping("/recommend")
    public String recommendPage(){
        return "recommend";
    }
    @GetMapping("/recommendation/ptag/{title}")
    public String recByPtagPage(@PathVariable String title, Model model){
        model.addAttribute("title", title);
        return "rec_byPtag";
    }
    @GetMapping("/ptag")
    public String allPtagPage( ){
        return "all_preferenceTag";
    }
    @GetMapping("/planning")
    public String planningPage(Model model,HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
        }
        return "planning_local";
    }
    @GetMapping("/planning/{id}")
    public String planningDetailPage(@PathVariable Long id,Model model,HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
        }
        return "planning_detail";
    }
    @GetMapping("/recommendation/{id}")
    public String recommendation_card(@PathVariable Long id, Model model,HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
        }
        model.addAttribute("recId", id);
        return "recommendation_card";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Kiểm tra nếu session đã có thông tin người dùng
        if (session.getAttribute("loggedInUser") != null) {
            System.out.println((UserDto) session.getAttribute("loggedInUser"));
            return "redirect:/home"; // Nếu đã đăng nhập thì chuyển hướng về trang Dashboard
        }
        return "login"; // Nếu chưa đăng nhập thì hiển thị trang login
    }

    @GetMapping("/sign_up")
    public String showSignUpPage(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            System.out.println(session.getAttribute("loggedInUser"));
            return "redirect:/home"; // Nếu đã đăng nhập thì chuyển hướng về trang Dashboard
        }
        return "sign_up";
    }

    @GetMapping(value = {"/social/posts"})
    public String showPostsPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
        }
        return "posts";
    }

    @GetMapping(value = {"/profile"})
    public String showProfilePage(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
            return "profile";// Nếu đã đăng nhập thì chuyển hướng về trang Dashboard
        }
        return "redirect:/login";
    }

    @GetMapping(value = {"/social"})
    public String showSocialIndexPage() {
        return "social_index";
    }

    @GetMapping(value = "/social/posts/{id}")
    public String showPostCardPage(@PathVariable Long id, Model model, HttpSession session) {
        // You can now use the 'id' variable
        System.out.println("Social ID: " + id);
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
            model.addAttribute("userRole", ud.getRole());
        }

        model.addAttribute("postId", id);
        return "post_card"; // The name of the view or template to render
    }
    @GetMapping(value = "/social/update/{id}")
    public String showPostUpdatePage(@PathVariable Long id, Model model, HttpSession session) {
        
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
            
        }
        model.addAttribute("postId", id);
        return "editPost"; // The name of the view or template to render
    }
    @GetMapping(value = {"/social/popular-posts"})
    public String showSocialPage() {
        return "popular_posts";
    }
    @GetMapping(value = {"/social/tag"})
    public String showPostTagPage() {
        
        return "postTag";
    }
    @GetMapping(value = {"/social/posts/tag/{tagName}"})
    public String showSocialPage(@PathVariable String tagName, Model model) {
        model.addAttribute("tagName", tagName);
        return "post_byTag";
    }
    @GetMapping(value = {"/social/posts/your-posts"})
    public String showYourPostPage( Model model,HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            UserDto ud = (UserDto) session.getAttribute("loggedInUser");
            model.addAttribute("userId", ud.getId());
            return "your_post";
        }
        return "redirect:/login";
    }
    @GetMapping(value = {"/social/posts/search"})
    public String showSearchPostPage(@RequestParam String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        return "search_post";
    }
    @GetMapping(value = {"/recommendation/search"})
    public String showSearchPage(@RequestParam String keyword, Model model) {
        model.addAttribute("keyword", keyword);
        return "search_recommendation";
    }
}
