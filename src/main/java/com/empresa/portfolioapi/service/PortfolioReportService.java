package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.dto.PortfolioReportResponse;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.repository.ProjectAllocationRepository;
import com.empresa.portfolioapi.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioReportService {

    private final ProjectRepository projectRepository;
    private final ProjectAllocationRepository allocationRepository;

    public PortfolioReportResponse generate() {
        Map<ProjectStatus, Long> projectsByStatus = new EnumMap<>(ProjectStatus.class);
        Map<ProjectStatus, BigDecimal> totalBudgetByStatus = new EnumMap<>(ProjectStatus.class);

        for (ProjectStatus status : ProjectStatus.values()) {
            List<Project> projects = projectRepository.findByStatus(status);

            projectsByStatus.put(status, (long) projects.size());

            BigDecimal totalBudget = projects.stream()
                    .map(Project::getTotalBudget)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalBudgetByStatus.put(status, totalBudget);
        }

        List<Project> closedProjects = projectRepository
                .findByStatusAndActualEndDateIsNotNull(ProjectStatus.ENCERRADO);

        Double averageDuration = closedProjects.isEmpty()
                ? null
                : closedProjects.stream()
                .mapToLong(project -> ChronoUnit.DAYS.between(
                        project.getStartDate(),
                        project.getActualEndDate()
                ))
                .average()
                .orElse(0.0);

        if (averageDuration != null) {
            averageDuration = BigDecimal.valueOf(averageDuration)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return new PortfolioReportResponse(
                projectsByStatus,
                totalBudgetByStatus,
                averageDuration,
                allocationRepository.countDistinctAllocatedMembers()
        );
    }
}