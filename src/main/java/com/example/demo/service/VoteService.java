/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.request.VoteRequest;

/**
 *
 * @author pc
 */
public interface VoteService {
    String voteRec(VoteRequest vq);
    boolean isRecVoteByUser(Long recId, Long userId);
}
