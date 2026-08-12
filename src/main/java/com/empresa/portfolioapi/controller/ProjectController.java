package com.empresa.portfolioapi.controller;

import com.empresa.portfolioapi.dto.ProjectCreateRequest;
import com.empresa.portfolioapi.dto.ProjectResponse;
import com.empresa.portfolioapi.dto.ProjectStatusUpdateRequest;
import com.empresa.portfolioapi.dto.ProjectUpdateRequest;
import com.empresa.portfolioapi.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.empresa.portfolioapi.enums.ProjectStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        ProjectResponse response = projectService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) BigDecimal minimumBudget,
            @RequestParam(required = false) BigDecimal maximumBudget,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDateTo,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(
                projectService.findAll(
                        name,
                        status,
                        managerId,
                        minimumBudget,
                        maximumBudget,
                        startDateFrom,
                        startDateTo,
                        pageable
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProjectStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(projectService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);

        return ResponseEntity.noContent().build();
    }
}