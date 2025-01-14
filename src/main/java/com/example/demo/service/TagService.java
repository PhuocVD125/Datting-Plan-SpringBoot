/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.dto.TagResponse;
import com.example.demo.dto.request.TagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author pc
 */
public interface TagService {
    Page<TagResponse> findTagByTitle(String keyword,Pageable pageable);
    Page<TagResponse> getAllTAg(Pageable pageable);
    String updateTag(Long id,TagDto tagDto);
    String deleteTag(Long id);
}
