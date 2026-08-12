package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.client.ExternalMemberResponse;
import com.empresa.portfolioapi.client.MemberExternalClient;
import com.empresa.portfolioapi.dto.AllocationRequest;
import com.empresa.portfolioapi.entity.Member;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.entity.ProjectAllocation;
import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.exception.BusinessException;
import com.empresa.portfolioapi.exception.ResourceNotFoundException;
import com.empresa.portfolioapi.repository.MemberRepository;
import com.empresa.portfolioapi.repository.ProjectAllocationRepository;
import com.empresa.portfolioapi.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AllocationService {

    private static final int MAX_MEMBERS_PER_PROJECT = 10;
    private static final int MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectAllocationRepository allocationRepository;
    private final MemberExternalClient memberExternalClient;

    public void allocate(Long projectId, AllocationRequest request) {
        Project project = findProject(projectId);
        Member member = findAndSyncMember(request.memberId());

        if (!"funcionário".equalsIgnoreCase(member.getRole())) {
            throw new BusinessException(
                    "Apenas membros com atribuição funcionário podem ser alocados em projetos."
            );
        }

        if (allocationRepository.existsByProjectIdAndMemberId(projectId, member.getId())) {
            throw new BusinessException("Este membro já está alocado no projeto.");
        }

        if (allocationRepository.countByProjectId(projectId) >= MAX_MEMBERS_PER_PROJECT) {
            throw new BusinessException("Um projeto pode ter no máximo 10 membros alocados.");
        }

        long activeProjects = allocationRepository.countActiveProjectsByMemberId(
                member.getId(),
                List.of(ProjectStatus.ENCERRADO, ProjectStatus.CANCELADO)
        );

        if (activeProjects >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
            throw new BusinessException(
                    "Um membro não pode estar alocado em mais de 3 projetos ativos."
            );
        }

        ProjectAllocation allocation = new ProjectAllocation();
        allocation.setProject(project);
        allocation.setMember(member);
        allocation.setAllocationDate(LocalDate.now());

        allocationRepository.save(allocation);
    }

    public void remove(Long projectId, Long memberId) {
        ProjectAllocation allocation = allocationRepository
                .findAll()
                .stream()
                .filter(item -> item.getProject().getId().equals(projectId))
                .filter(item -> item.getMember().getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Alocação de membro não encontrada."
                ));

        allocationRepository.delete(allocation);
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));
    }

    private Member findAndSyncMember(Long id) {
        ExternalMemberResponse externalMember = memberExternalClient.findById(id);

        return memberRepository.findById(id)
                .map(member -> {
                    member.setName(externalMember.name());
                    member.setRole(externalMember.role());
                    return memberRepository.save(member);
                })
                .orElseGet(() -> {
                    Member member = new Member();
                    member.setId(externalMember.id());
                    member.setName(externalMember.name());
                    member.setRole(externalMember.role());
                    return memberRepository.save(member);
                });
    }
}