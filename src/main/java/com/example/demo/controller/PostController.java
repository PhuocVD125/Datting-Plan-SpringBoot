/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.config.FileSaveConfig;
import com.example.demo.dto.request.LikeRequest;
import com.example.demo.dto.PostCardDto;
import com.example.demo.dto.request.PostCreateDto;
import com.example.demo.dto.PostDto;
import com.example.demo.dto.request.PostUpdateDto;
import com.example.demo.service.LikeService;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author pc
 */
@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final FileSaveConfig fileSaveConfig;
    private final LikeService likeService;

    @Autowired
    public PostController(PostService postService, FileSaveConfig fileSaveConfig, LikeService likeService) {
        this.postService = postService;
        this.fileSaveConfig = fileSaveConfig;
        this.likeService = likeService;
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<String> createPost(
            @RequestParam("pd") String pdJson,
            @RequestPart(value = "image", required = false) MultipartFile[] files
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        PostCreateDto pd;

        try {
            List<String> urls=new ArrayList<>();
            // Parse JSON string to PostCreateDto object
            pd = objectMapper.readValue(pdJson, PostCreateDto.class);
            if(files!=null){
               for(MultipartFile mt:files)
                urls.add(fileSaveConfig.saveImage(mt));
               pd.setImage(urls); 
            }
            

            String response = postService.createPost(pd);
            if ("New Post Created".equals(response)) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException ex) {
            Logger.getLogger(PostController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Failed to process request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @RequestParam("pd") String pdJson,
            @RequestPart(value = "image", required = false) MultipartFile[] files
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        PostUpdateDto pd;

        try {
            List<String> urls=new ArrayList<>();
            // Parse JSON string to PostCreateDto object
            pd = objectMapper.readValue(pdJson, PostUpdateDto.class);
            if(files!=null){
               for(MultipartFile mt:files)
                urls.add(fileSaveConfig.saveImage(mt));
               pd.setImage(urls); 
            }

            String response = postService.updatePost(id,pd);
            if ("Update post successfully".equals(response)) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IOException ex) {
            Logger.getLogger(PostController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Failed to process request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("src/main/resources/static/images/" + filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // hoặc loại hình ảnh thích hợp
                        .body(resource);
            } else {
                throw new IOException("Image not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        String response;
        response = postService.deletePost(id);
        if (response.equals("Deleted Post")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostCardDto> getPostById(@PathVariable Long id) {
        PostCardDto p = postService.getPostById(id);
        if (p != null) {
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("rec/{id}")
    public ResponseEntity<Page<PostDto>> getPostByRecId(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
       Pageable pageable = PageRequest.of(page, size, sort);
       return ResponseEntity.ok(postService.getPostRecIdPageable(id, pageable));
    }
    @GetMapping("user/{id}")
    public ResponseEntity<Page<PostDto>> getPostByUserId(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
       Pageable pageable = PageRequest.of(page, size, sort);
       return ResponseEntity.ok(postService.getPostUserIdPageable(id, pageable));
    }
    @GetMapping("/search")
    public ResponseEntity<Page<PostDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(postService.searchPost(pageable, keyword));
    }

    @GetMapping("/")
    public ResponseEntity<Page<PostDto>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(postService.getAllPostPageable(pageable));
    }

    @GetMapping("/most-liked")
    public ResponseEntity<List<PostDto>> getMostLikedPosts() {
        List<PostDto> response = postService.getTop8MostLikedPosts();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/like")
    public ResponseEntity<String> likePost(@RequestBody LikeRequest likeRequest) {
        String response = likeService.likePost(likeRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}/isLiked/{userId}")
    public ResponseEntity<Boolean> isPostLiked(@PathVariable Long postId, @PathVariable Long userId) {
        boolean isLiked = likeService.isPostLikedByUser(postId, userId);
        return ResponseEntity.ok(isLiked);
    }
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<Page<PostDto>> getPostsByTagName(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "time") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(postService.getPostsByTagName(tagName,pageable));
    }
}
