package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.dto.ProjectCreateRequest;
import com.empresa.portfolioapi.dto.ProjectResponse;
import com.empresa.portfolioapi.dto.ProjectStatusUpdateRequest;
import com.empresa.portfolioapi.dto.ProjectUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.empresa.portfolioapi.enums.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProjectService {

    ProjectResponse create(ProjectCreateRequest request);

    ProjectResponse findById(Long id);

    Page<ProjectResponse> findAll(
            String name,
            ProjectStatus status,
            Long managerId,
            BigDecimal minimumBudget,
            BigDecimal maximumBudget,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            Pageable pageable
    );

    ProjectResponse update(Long id, ProjectUpdateRequest request);

    ProjectResponse updateStatus(Long id, ProjectStatusUpdateRequest request);

    void delete(Long id);
}