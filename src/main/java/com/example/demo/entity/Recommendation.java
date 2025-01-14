/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author pc
 */
@Entity
@Table(name = "recommendation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String location;
    private String email;
    @Column
    private String title;
    @Column
    private LocalTime startTime;
    private LocalTime endTime;
    private String address;
    @Column(length = 4000)
    private String description;
    @Column
    private String recommendTime;
    @Column
    private Long rating;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(nullable = true)
    private Double minBudget;

    @Column(nullable = true)
    private Double maxBudget;
    @Column
    private Boolean isActive;
    @Column(length = 2000)
    private List<String> image;
    @ManyToMany()
    @JoinTable(name = "recommendation_preference_tags",joinColumns = @JoinColumn(name ="recommendation_id",referencedColumnName = "id"),inverseJoinColumns = @JoinColumn(name ="tag_id" ,referencedColumnName = "id"),uniqueConstraints = @UniqueConstraint(columnNames = {"recommendation_id","tag_id"}))
    private Set<PreferenceTag> tags;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recommendation_id")
    private List<Vote> votes;
    @OneToMany(mappedBy ="recommendationPost",cascade = CascadeType.ALL )
    List<Post> postRecommendations;
    @OneToMany(mappedBy ="recommendation",cascade = CascadeType.ALL )
    List<PlanningRecommendation> planningRecommendations;   
}
