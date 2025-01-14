/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.PostCardDto;
import com.example.demo.dto.request.PostCreateDto;
import com.example.demo.dto.PostDto;
import com.example.demo.dto.request.PostUpdateDto;
import com.example.demo.entity.Post;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 *
 * @author pc
 */
public interface PostService {
    String createPost(PostCreateDto postDto);
    String updatePost(Long postId,PostUpdateDto postDto);
    String deletePost(Long postId);
    Page<PostDto> getAllPostPageable(Pageable pageable);
    PostCardDto getPostById(Long id);
    Page<PostDto> searchPost(Pageable pageable,String keyword);
    List<PostDto> getTop8MostLikedPosts();
    Page<PostDto> getPostsByTagName(String tagName, Pageable pageable);
    Page<PostDto> getPostRecIdPageable(Long recId,Pageable pageable);
    Page<PostDto> getPostUserIdPageable(Long userId,Pageable pageable);

}
