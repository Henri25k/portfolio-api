package com.empresa.portfolioapi.mapper;

import com.empresa.portfolioapi.entity.Member;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.enums.RiskClassification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectMapperTest {

    private final ProjectMapper projectMapper = new ProjectMapper();

    @Test
    void shouldCalculateLowRisk() {
        Project project = createProject(
                new BigDecimal("100000"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 4, 1)
        );

        RiskClassification risk = projectMapper.toResponse(project).riskClassification();

        assertEquals(RiskClassification.BAIXO, risk);
    }

    @Test
    void shouldCalculateMediumRisk() {
        Project project = createProject(
                new BigDecimal("250000"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 5, 1)
        );

        RiskClassification risk = projectMapper.toResponse(project).riskClassification();

        assertEquals(RiskClassification.MEDIO, risk);
    }

    @Test
    void shouldCalculateHighRiskByBudget() {
        Project project = createProject(
                new BigDecimal("500001"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 1)
        );

        RiskClassification risk = projectMapper.toResponse(project).riskClassification();

        assertEquals(RiskClassification.ALTO, risk);
    }

    @Test
    void shouldCalculateHighRiskByDeadline() {
        Project project = createProject(
                new BigDecimal("50000"),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 8, 1)
        );

        RiskClassification risk = projectMapper.toResponse(project).riskClassification();

        assertEquals(RiskClassification.ALTO, risk);
    }

    private Project createProject(
            BigDecimal totalBudget,
            LocalDate startDate,
            LocalDate expectedEndDate
    ) {
        Member manager = new Member();
        manager.setId(1L);
        manager.setName("Ana Souza");

        Project project = new Project();
        project.setId(1L);
        project.setName("Projeto de teste");
        project.setStartDate(startDate);
        project.setExpectedEndDate(expectedEndDate);
        project.setTotalBudget(totalBudget);
        project.setDescription("Descrição de teste");
        project.setManager(manager);
        project.setStatus(ProjectStatus.EM_ANALISE);

        return project;
    }
}