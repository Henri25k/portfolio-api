package com.empresa.portfolioapi.controller;

import com.empresa.portfolioapi.dto.PortfolioReportResponse;
import com.empresa.portfolioapi.service.PortfolioReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class PortfolioReportController {

    private final PortfolioReportService portfolioReportService;

    @GetMapping("/portfolio-summary")
    public ResponseEntity<PortfolioReportResponse> generatePortfolioSummary() {
        return ResponseEntity.ok(portfolioReportService.generate());
    }
}