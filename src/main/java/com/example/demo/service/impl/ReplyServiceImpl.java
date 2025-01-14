/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.request.ReplyRequestDto;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Reply;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ReplyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ReplyService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class ReplyServiceImpl implements ReplyService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReplyServiceImpl(CommentRepository commentRepository, ReplyRepository replyRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.replyRepository = replyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String saveReply(ReplyRequestDto rqDto) {
        Reply reply = new Reply();
        reply.setContent(rqDto.getContent());
        reply.setTime(rqDto.getTime());
        User user = userRepository.findById(rqDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        reply.setUserReply(user);
        if (rqDto.getCommentId() != null) {
            // Reply to a comment 
            Comment comment = commentRepository.findById(rqDto.getCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
            reply.setParentReply(null); // No parent reply
//        comment.getReplys().add(reply);
            reply.setParentComment(comment);
        } else if (rqDto.getParentReplyUserId() != null) {
            // Reply to another reply
            Reply parentReply = replyRepository.findById(rqDto.getParentReplyUserId()).get();
            
            reply.setParentReply(parentReply);
        }
        replyRepository.save(reply);
        return "Reply success";
    }

    @Override
    public String deleteReply(Long id) {
        Optional<Reply> r=replyRepository.findById(id);
        if(r.isPresent())
        {
            replyRepository.delete(r.get());
            return "Delete Reply";
        }
        else return "Cant find Reply";
    }

}
