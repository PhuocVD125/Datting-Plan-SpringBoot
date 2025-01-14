/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.request.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.entity.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CommentService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String createComment(CommentRequest commentRequest) {
        Post post = postRepository.findById(commentRequest.getPostId()).get();
        User user = userRepository.findById(commentRequest.getUserId()).get();
        System.out.println(post.getId());
        System.out.println(user.getId());
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setTime(LocalDateTime.now()); // Set the current time
        comment.setPostComment(post); // Set the associated post
        comment.setUser(user); // Set the associated user
        commentRepository.save(comment);
        return "Create Comment";
    }

    @Override
    public String deleteComment(Long id) {
        Optional<Comment> c=commentRepository.findById(id);
        if(c.isPresent()){
            commentRepository.delete(commentRepository.findById(id).get());
            return "Deleted Comment";
        }
        return "Cant find Comment";
            
        
        
    }

}
