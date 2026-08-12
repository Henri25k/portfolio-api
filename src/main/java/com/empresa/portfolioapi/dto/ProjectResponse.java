package com.empresa.portfolioapi.dto;

import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.enums.RiskClassification;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        BigDecimal totalBudget,
        String description,
        Long managerId,
        String managerName,
        ProjectStatus status,
        RiskClassification riskClassification
) {
}