/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.dto.PreferenceTagResponse;
import com.example.demo.dto.PreferenceTagDto;
import com.example.demo.service.PreferenceTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author pc
 */
@RestController
@RequestMapping("/api/v1/ptag")
public class PreferenceTagController {
    @Autowired
    private PreferenceTagService preferenceTagService;
    @GetMapping("/")
    public ResponseEntity<Page<PreferenceTagResponse>> getAllPtag(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "desc") String direction){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(preferenceTagService.findAllTag(pageable));
    }

    // Phuoc

    @GetMapping("/search")
    public ResponseEntity<Page<PreferenceTagResponse>> searchTagsByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Tìm kiếm theo title
        Page<PreferenceTagResponse> result = preferenceTagService.searchTagsByTitle(title, pageable);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/add")
    public ResponseEntity<PreferenceTagResponse> createPreferenceTag(@RequestBody PreferenceTagDto preferenceTagDto) {
        PreferenceTagResponse response = preferenceTagService.createPreferenceTag(preferenceTagDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenceTagResponse> getPreferenceTagById(@PathVariable Long id) {
        PreferenceTagResponse response = preferenceTagService.getPreferenceTagById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PreferenceTagResponse> updatePreferenceTag(@PathVariable Long id, @RequestBody PreferenceTagDto preferenceTagDto) {
        PreferenceTagResponse response = preferenceTagService.updatePreferenceTag(id, preferenceTagDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreferenceTag(@PathVariable Long id) {
        preferenceTagService.deletePreferenceTag(id);
        return ResponseEntity.noContent().build();
    }
}
