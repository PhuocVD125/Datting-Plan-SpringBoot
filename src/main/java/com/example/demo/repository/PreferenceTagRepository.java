/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.repository;

import com.example.demo.entity.PreferenceTag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author pc
 */
public interface PreferenceTagRepository extends JpaRepository<PreferenceTag, Long>{
    PreferenceTag findByTitle(String title);
    Page<PreferenceTag> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
