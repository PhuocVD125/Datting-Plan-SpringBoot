//package com.example.demo.specification;
//
//import com.example.demo.entity.Recommendation;
//import org.springframework.data.jpa.domain.Specification;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Predicate;
//
//public class RecommendationSpecification {
//
//    public static Specification<Recommendation> filterRecommendations(
//            String location, LocalTime startTime, LocalTime endTime, Double minBudget, Double maxBudget) {
//
//        return (root, query, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            // Điều kiện location
//            if (location != null) {
//                predicates.add((Predicate) criteriaBuilder.or(
//                        criteriaBuilder.isNull(root.get("location")),
//                        criteriaBuilder.equal(root.get("location"), location),
//                        criteriaBuilder.equal(root.get("location"), "Select District - Select City")
//                ));
//            }
//
//            // Điều kiện startTime và endTime
//            if (startTime != null && endTime != null) {
//                predicates.add((Predicate) criteriaBuilder.or(
//                        criteriaBuilder.isNull(root.get("startTime")),
//                        criteriaBuilder.and(
//                                criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), startTime),
//                                criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), endTime)
//                        )
//                ));
//            }
//
//            // Điều kiện minBudget và maxBudget
//            if (minBudget != null && maxBudget != null) {
//                predicates.add((Predicate) criteriaBuilder.or(
//                        criteriaBuilder.and(
//                                criteriaBuilder.greaterThanOrEqualTo(root.get("minBudget"), minBudget),
//                                criteriaBuilder.lessThanOrEqualTo(root.get("maxBudget"), maxBudget)
//                        )
//                ));
//            }
//
//            // Điều kiện isActive
//            predicates.add((Predicate) criteriaBuilder.isTrue(root.get("isActive")));
//
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//
//        };
//    }
//}
