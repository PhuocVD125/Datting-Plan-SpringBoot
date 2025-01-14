/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author pc
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.likes l GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<Post> findTop8MostLikedPosts(Pageable pageable);
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE LOWER(t.title) = LOWER(:tagName)")
    Page<Post> findPostsByTagName(@Param("tagName") String tagName, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.recommendationPost.id = :recommendationId")
    Page<Post> findPostsByRecommendationId(@Param("recommendationId") Long recommendationId,Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.userPost.id = :userId")
    Page<Post> findPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
