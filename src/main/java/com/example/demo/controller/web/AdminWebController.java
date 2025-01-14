package com.example.demo.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping("/home")
    public String adminHomePage() {
        return "adminHome";
    }
    @GetMapping("/manage-user/create")
    public String adminCreateUserPage() {
        return "adminCreate";
    }
    @GetMapping("/ptag")
    public String preferencePage() {
        return "preference_tag";
    }
    @GetMapping("/manage-user/edit/{id}")
    public String adminEditUserPage(@PathVariable Long id,Model model) {
        model.addAttribute("eidId", id);
        return "edit_user";
    }
    @GetMapping("/recommendation")
    public String recommendPage() {
        return "recommend_manage_list";
    }


    @GetMapping("recommendation/create")
    public String createRecommendation() {
        return "recommend_manage_create";
    }

    @GetMapping("recommendation/update")
    public String updateRecommendation() {
        return "recommend_manage_update";
    }

    @GetMapping("/manage-post-tag")
    public String managePostTag() {
        return "manage_Post_Tag";
    }

    @GetMapping("/manage-comment")
    public String manageComment() {
        return "manage_Comment";
    }

    @GetMapping("/manage-post")
    public String managePost() {
        return "manage_Post";
    }

    @GetMapping("/manage-user")
    public String manageUser() {
        return "manage_Account_User";
    }
}
