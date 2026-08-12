package com.empresa.portfolioapi.dto;

import com.empresa.portfolioapi.enums.ProjectStatus;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReportResponse(
        Map<ProjectStatus, Long> projectsByStatus,
        Map<ProjectStatus, BigDecimal> totalBudgetByStatus,
        Double averageClosedProjectDurationDays,
        Long uniqueAllocatedMembers
) {
}