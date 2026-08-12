package com.empresa.portfolioapi.repository;

import com.empresa.portfolioapi.entity.ProjectAllocation;
import com.empresa.portfolioapi.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectAllocationRepository extends JpaRepository<ProjectAllocation, Long> {

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    long countByProjectId(Long projectId);

    @Query("""
            select count(distinct allocation.project.id)
            from ProjectAllocation allocation
            where allocation.member.id = :memberId
              and allocation.project.status not in :closedStatuses
            """)
    long countActiveProjectsByMemberId(
            @Param("memberId") Long memberId,
            @Param("closedStatuses") java.util.Collection<ProjectStatus> closedStatuses
    );
    @Query("select count(distinct allocation.member.id) from ProjectAllocation allocation")
    long countDistinctAllocatedMembers();
}