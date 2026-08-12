package com.empresa.portfolioapi.controller;

import com.empresa.portfolioapi.dto.AllocationRequest;
import com.empresa.portfolioapi.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectAllocationController {

    private final AllocationService allocationService;

    @PostMapping
    public ResponseEntity<Void> allocate(
            @PathVariable Long projectId,
            @Valid @RequestBody AllocationRequest request
    ) {
        allocationService.allocate(projectId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> remove(
            @PathVariable Long projectId,
            @PathVariable Long memberId
    ) {
        allocationService.remove(projectId, memberId);

        return ResponseEntity.noContent().build();
    }
}