package com.empresa.portfolioapi.repository;

import com.empresa.portfolioapi.entity.ProjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, Long> {
}