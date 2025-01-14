/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.dto.ReplyDto;
import com.example.demo.dto.request.ReplyRequestDto;
import com.example.demo.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author pc
 */@RestController
@RequestMapping("/api/v1/reply")

public class ReplyController {
    private final ReplyService replyService;
     @Autowired
    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }
    @PostMapping("/add")
    public ResponseEntity<String> createReply(@RequestBody ReplyRequestDto replyRequestDto) {
        try {
            String response = replyService.saveReply(replyRequestDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReply(@PathVariable Long id){
        String response=replyService.deleteReply(id);
        return ResponseEntity.ok(response);
    }
}
