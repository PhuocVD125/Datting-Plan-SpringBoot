/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.PreferenceTagResponse;
import com.example.demo.dto.PreferenceTagDto;
import com.example.demo.entity.PreferenceTag;
import com.example.demo.repository.PreferenceTagRepository;
import com.example.demo.service.PreferenceTagService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
/**
 *
 * @author pc
 */
@Service
public class PreferenceTagServiceImpl implements PreferenceTagService {
    private final PreferenceTagRepository repo ;
    private final ModelMapper modelMapper;
    @Autowired
    public PreferenceTagServiceImpl(PreferenceTagRepository repo, ModelMapper modelMapper) {
        this.repo = repo;
        this.modelMapper = modelMapper;
    }

    @Override
    public PreferenceTag findByTitle(String title) {
        return repo.findByTitle(title);
    }

    @Override
    public Page<PreferenceTagResponse> findAllTag(Pageable pageable) {
        // Lấy danh sách phân trang từ repository
        Page<PreferenceTag> tags = repo.findAll(pageable);

        // Ánh xạ từ PreferenceTag sang PreferenceTagResponse
        return tags.map(tag -> {
            PreferenceTagResponse response = new PreferenceTagResponse();
            response.setId(tag.getId());
            response.setTitle(tag.getTitle());
            return response;
        });
    }

    // Phuoc

    @Override
    public Page<PreferenceTagResponse> searchTagsByTitle(String title, Pageable pageable) {
        // Tìm kiếm theo title với phân trang
        Page<PreferenceTag> tags = repo.findByTitleContainingIgnoreCase(title, pageable);

        // Chuyển đổi từ PreferenceTag sang PreferenceTagResponse
        return tags.map(tag -> {
            PreferenceTagResponse response = new PreferenceTagResponse();
            response.setId(tag.getId());
            response.setTitle(tag.getTitle());
            return response;
        });
    }

    @Override
    public PreferenceTagResponse createPreferenceTag(PreferenceTagDto preferenceTagDto) {
        if (repo.findByTitle(preferenceTagDto.getTitle()) != null) {
            throw new IllegalArgumentException("Tag title must be unique");
        }
        PreferenceTag tag = modelMapper.map(preferenceTagDto, PreferenceTag.class);
        PreferenceTag savedTag = repo.save(tag);
        return modelMapper.map(savedTag, PreferenceTagResponse.class);
    }

    @Override
    public PreferenceTagResponse updatePreferenceTag(Long id, PreferenceTagDto preferenceTagDto) {
        PreferenceTag existingTag = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        existingTag.setTitle(preferenceTagDto.getTitle());
        PreferenceTag updatedTag = repo.save(existingTag);
        return modelMapper.map(updatedTag, PreferenceTagResponse.class);
    }

    @Override
    public void deletePreferenceTag(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Tag not found");
        }
        repo.deleteById(id);
    }

    @Override
    public PreferenceTagResponse getPreferenceTagById(Long id) {
        PreferenceTag tag = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));
        return modelMapper.map(tag, PreferenceTagResponse.class);
    }
    
}
