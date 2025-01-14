/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.PreferenceTagResponse;
import com.example.demo.dto.PreferenceTagDto;
import com.example.demo.entity.PreferenceTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author pc
 */
public interface PreferenceTagService {
    PreferenceTag findByTitle(String title);
    Page<PreferenceTagResponse> findAllTag(Pageable pageable);

    Page<PreferenceTagResponse> searchTagsByTitle(String title, Pageable pageable);

    PreferenceTagResponse createPreferenceTag(PreferenceTagDto preferenceTagDto);
    PreferenceTagResponse updatePreferenceTag(Long id, PreferenceTagDto preferenceTagDto);
    void deletePreferenceTag(Long id);
    PreferenceTagResponse getPreferenceTagById(Long id);
}
