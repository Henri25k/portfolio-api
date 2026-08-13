package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.client.ExternalMemberResponse;
import com.empresa.portfolioapi.client.MemberExternalClient;
import com.empresa.portfolioapi.dto.AllocationRequest;
import com.empresa.portfolioapi.entity.Member;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.exception.BusinessException;
import com.empresa.portfolioapi.repository.MemberRepository;
import com.empresa.portfolioapi.repository.ProjectAllocationRepository;
import com.empresa.portfolioapi.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectAllocationRepository allocationRepository;

    @Mock
    private MemberExternalClient memberExternalClient;

    @InjectMocks
    private AllocationService allocationService;

    @BeforeEach
    void setUp() {
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldNotAllocateNonEmployeeMember() {
        stubProjectAndMember("gerente");

        assertThrows(
                BusinessException.class,
                () -> allocationService.allocate(1L, new AllocationRequest(1L))
        );
    }

    @Test
    void shouldNotAllocateMemberTwiceInSameProject() {
        stubProjectAndMember("funcionário");

        when(allocationRepository.existsByProjectIdAndMemberId(1L, 1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> allocationService.allocate(1L, new AllocationRequest(1L))
        );
    }

    @Test
    void shouldNotAllocateMoreThanTenMembersInProject() {
        stubProjectAndMember("funcionário");

        when(allocationRepository.existsByProjectIdAndMemberId(1L, 1L))
                .thenReturn(false);
        when(allocationRepository.countByProjectId(1L))
                .thenReturn(10L);

        assertThrows(
                BusinessException.class,
                () -> allocationService.allocate(1L, new AllocationRequest(1L))
        );
    }

    @Test
    void shouldNotAllocateMemberInMoreThanThreeActiveProjects() {
        stubProjectAndMember("funcionário");

        when(allocationRepository.existsByProjectIdAndMemberId(1L, 1L))
                .thenReturn(false);
        when(allocationRepository.countByProjectId(1L))
                .thenReturn(1L);
        when(allocationRepository.countActiveProjectsByMemberId(any(), any()))
                .thenReturn(3L);

        assertThrows(
                BusinessException.class,
                () -> allocationService.allocate(1L, new AllocationRequest(1L))
        );
    }

    @Test
    void shouldAllocateEmployeeMemberWhenRulesAreSatisfied() {
        stubProjectAndMember("funcionário");

        when(allocationRepository.existsByProjectIdAndMemberId(1L, 1L))
                .thenReturn(false);
        when(allocationRepository.countByProjectId(1L))
                .thenReturn(1L);
        when(allocationRepository.countActiveProjectsByMemberId(any(), any()))
                .thenReturn(2L);

        assertDoesNotThrow(
                () -> allocationService.allocate(1L, new AllocationRequest(1L))
        );
    }

    private void stubProjectAndMember(String role) {
        Project project = new Project();
        project.setId(1L);

        Member member = new Member();
        member.setId(1L);
        member.setName("Ana Souza");
        member.setRole(role);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberExternalClient.findById(1L))
                .thenReturn(new ExternalMemberResponse(1L, "Ana Souza", role));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
    }
}