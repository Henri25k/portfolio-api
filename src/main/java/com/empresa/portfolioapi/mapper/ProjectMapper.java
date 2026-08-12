package com.empresa.portfolioapi.mapper;

import com.empresa.portfolioapi.dto.ProjectResponse;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.RiskClassification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getExpectedEndDate(),
                project.getActualEndDate(),
                project.getTotalBudget(),
                project.getDescription(),
                project.getManager().getId(),
                project.getManager().getName(),
                project.getStatus(),
                calculateRisk(project)
        );
    }

    private RiskClassification calculateRisk(Project project) {
        long durationMonths = ChronoUnit.MONTHS.between(
                project.getStartDate(),
                project.getExpectedEndDate()
        );

        BigDecimal lowRiskLimit = new BigDecimal("100000");
        BigDecimal highRiskLimit = new BigDecimal("500000");

        if (project.getTotalBudget().compareTo(highRiskLimit) > 0 || durationMonths > 6) {
            return RiskClassification.ALTO;
        }

        if (project.getTotalBudget().compareTo(lowRiskLimit) <= 0 && durationMonths <= 3) {
            return RiskClassification.BAIXO;
        }

        return RiskClassification.MEDIO;
    }
}