package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.dto.PortfolioReportResponse;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.repository.ProjectAllocationRepository;
import com.empresa.portfolioapi.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectAllocationRepository allocationRepository;

    @InjectMocks
    private PortfolioReportService portfolioReportService;

    @Test
    void shouldGenerateReportWithProjectsAndMembers() {
        Project project = new Project();
        project.setTotalBudget(new BigDecimal("85000"));

        when(projectRepository.findByStatus(any(ProjectStatus.class)))
                .thenAnswer(invocation -> {
                    ProjectStatus status = invocation.getArgument(0);

                    if (status == ProjectStatus.EM_ANALISE) {
                        return List.of(project);
                    }

                    return List.of();
                });

        when(projectRepository.findByStatusAndActualEndDateIsNotNull(
                ProjectStatus.ENCERRADO
        )).thenReturn(List.of());

        when(allocationRepository.countDistinctAllocatedMembers()).thenReturn(2L);

        PortfolioReportResponse response = portfolioReportService.generate();

        assertEquals(1L, response.projectsByStatus().get(ProjectStatus.EM_ANALISE));
        assertEquals(
                new BigDecimal("85000"),
                response.totalBudgetByStatus().get(ProjectStatus.EM_ANALISE)
        );
        assertEquals(2L, response.uniqueAllocatedMembers());
    }

    @Test
    void shouldCalculateAverageDurationForClosedProjects() {
        Project closedProject = new Project();
        closedProject.setStartDate(LocalDate.of(2026, 1, 1));
        closedProject.setActualEndDate(LocalDate.of(2026, 1, 11));

        when(projectRepository.findByStatus(any(ProjectStatus.class)))
                .thenReturn(List.of());

        when(projectRepository.findByStatusAndActualEndDateIsNotNull(
                ProjectStatus.ENCERRADO
        )).thenReturn(List.of(closedProject));

        when(allocationRepository.countDistinctAllocatedMembers()).thenReturn(0L);

        PortfolioReportResponse response = portfolioReportService.generate();

        assertEquals(10.0, response.averageClosedProjectDurationDays());
    }
}