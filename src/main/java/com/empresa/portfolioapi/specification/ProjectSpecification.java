package com.empresa.portfolioapi.specification;

import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Project> hasStatus(ProjectStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Project> hasManagerId(Long managerId) {
        return (root, query, criteriaBuilder) -> {
            if (managerId == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("manager").get("id"), managerId);
        };
    }

    public static Specification<Project> budgetGreaterOrEqual(BigDecimal minimumBudget) {
        return (root, query, criteriaBuilder) -> {
            if (minimumBudget == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalBudget"),
                    minimumBudget
            );
        };
    }

    public static Specification<Project> budgetLessOrEqual(BigDecimal maximumBudget) {
        return (root, query, criteriaBuilder) -> {
            if (maximumBudget == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("totalBudget"),
                    maximumBudget
            );
        };
    }

    public static Specification<Project> startDateAfterOrEqual(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null) {
                return null;
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("startDate"),
                    startDate
            );
        };
    }

    public static Specification<Project> startDateBeforeOrEqual(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null) {
                return null;
            }

            return criteriaBuilder.lessThanOrEqualTo(
                    root.get("startDate"),
                    startDate
            );
        };
    }
}