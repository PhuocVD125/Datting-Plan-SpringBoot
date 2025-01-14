/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.dto.TagResponse;
import com.example.demo.dto.request.TagDto;
import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author pc
 */
@Service
public class TagServiceImpl implements TagService{
    private final TagRepository tagRepo ;
    @Autowired
    public TagServiceImpl(TagRepository tagRepo) {
        this.tagRepo = tagRepo;
    }
    

    @Override
    public Page<TagResponse> findTagByTitle(String keyword,Pageable pageable) {
        return tagRepo.findByTitleContainingIgnoreCase(keyword, pageable).map(this::toResponse);
    }
    public TagResponse toResponse(Tag tag){
        TagResponse t=new TagResponse(tag.getId(), tag.getTitle());
        return t;
    }

    @Override
    public Page<TagResponse> getAllTAg(Pageable pageable) {
        return tagRepo.findAll(pageable).map(this::toResponse);
    }

    @Override
    public String updateTag(Long id, TagDto tagDto) {
        Tag t=tagRepo.findById(id).get();
        t.setTitle(tagDto.getTitle());
        tagRepo.save(t);
        return "Update Tag Successfully";
    }

    @Override
    public String deleteTag(Long id) {
        tagRepo.deleteById(id);
        return "Delete Tag Successfully";
    }
}
