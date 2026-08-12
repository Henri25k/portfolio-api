package com.empresa.portfolioapi.repository;

import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByStatusAndActualEndDateIsNotNull(ProjectStatus status);
}