package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.dto.ProjectStatusUpdateRequest;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.exception.BusinessException;
import com.empresa.portfolioapi.repository.MemberRepository;
import com.empresa.portfolioapi.repository.ProjectRepository;
import com.empresa.portfolioapi.mapper.ProjectMapper;
import com.empresa.portfolioapi.client.MemberExternalClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.empresa.portfolioapi.repository.ProjectAllocationRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private MemberExternalClient memberExternalClient;

    @Mock
    private ProjectAllocationRepository allocationRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void shouldAllowNextStatusTransition() {
        Project project = createProject(ProjectStatus.EM_ANALISE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateStatus(
                1L,
                new ProjectStatusUpdateRequest(ProjectStatus.ANALISE_REALIZADA)
        );

        assertEquals(ProjectStatus.ANALISE_REALIZADA, project.getStatus());
    }

    @Test
    void shouldNotAllowSkippingStatus() {
        Project project = createProject(ProjectStatus.EM_ANALISE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(
                BusinessException.class,
                () -> projectService.updateStatus(
                        1L,
                        new ProjectStatusUpdateRequest(ProjectStatus.INICIADO)
                )
        );
    }

    @Test
    void shouldAllowCancellationAtAnyTime() {
        Project project = createProject(ProjectStatus.PLANEJADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateStatus(
                1L,
                new ProjectStatusUpdateRequest(ProjectStatus.CANCELADO)
        );

        assertEquals(ProjectStatus.CANCELADO, project.getStatus());
    }

    @Test
    void shouldNotChangeCancelledProjectStatus() {
        Project project = createProject(ProjectStatus.CANCELADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(
                BusinessException.class,
                () -> projectService.updateStatus(
                        1L,
                        new ProjectStatusUpdateRequest(ProjectStatus.EM_ANALISE)
                )
        );
    }

    @Test
    void shouldSetActualEndDateWhenProjectIsClosed() {
        Project project = createProject(ProjectStatus.EM_ANDAMENTO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateStatus(
                1L,
                new ProjectStatusUpdateRequest(ProjectStatus.ENCERRADO)
        );

        assertEquals(ProjectStatus.ENCERRADO, project.getStatus());
        assertEquals(LocalDate.now(), project.getActualEndDate());
    }

    @Test
    void shouldNotDeleteProjectInProgress() {
        Project project = createProject(ProjectStatus.EM_ANDAMENTO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(
                BusinessException.class,
                () -> projectService.delete(1L)
        );
    }

    private Project createProject(ProjectStatus status) {
        Project project = new Project();
        project.setId(1L);
        project.setStatus(status);

        return project;
    }
    @Test
    void shouldAllowDeletingProjectInAnalysis() {
        Project project = createProject(ProjectStatus.EM_ANALISE);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.delete(1L);

        org.mockito.Mockito.verify(projectRepository).delete(project);
    }

    @Test
    void shouldNotDeleteClosedProject() {
        Project project = createProject(ProjectStatus.ENCERRADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(
                BusinessException.class,
                () -> projectService.delete(1L)
        );
    }

    @Test
    void shouldNotChangeClosedProjectStatus() {
        Project project = createProject(ProjectStatus.ENCERRADO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThrows(
                BusinessException.class,
                () -> projectService.updateStatus(
                        1L,
                        new ProjectStatusUpdateRequest(ProjectStatus.EM_ANALISE)
                )
        );
    }

    @Test
    void shouldNotAllowEndDateBeforeStartDate() {
        com.empresa.portfolioapi.dto.ProjectCreateRequest request =
                new com.empresa.portfolioapi.dto.ProjectCreateRequest(
                        "Projeto inválido",
                        LocalDate.of(2026, 5, 10),
                        LocalDate.of(2026, 5, 1),
                        new java.math.BigDecimal("1000"),
                        "Descrição válida",
                        1L
                );

        assertThrows(
                BusinessException.class,
                () -> projectService.create(request)
        );
    }
    @Test
    void shouldNotStartProjectWithoutAllocatedMembers() {
        Project project = createProject(ProjectStatus.ANALISE_APROVADA);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.countByProjectId(1L)).thenReturn(0L);

        assertThrows(
                BusinessException.class,
                () -> projectService.updateStatus(
                        1L,
                        new ProjectStatusUpdateRequest(ProjectStatus.INICIADO)
                )
        );
    }

    @Test
    void shouldStartProjectWithAtLeastOneAllocatedMember() {
        Project project = createProject(ProjectStatus.ANALISE_APROVADA);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(allocationRepository.countByProjectId(1L)).thenReturn(1L);
        when(projectRepository.save(project)).thenReturn(project);

        projectService.updateStatus(
                1L,
                new ProjectStatusUpdateRequest(ProjectStatus.INICIADO)
        );

        assertEquals(ProjectStatus.INICIADO, project.getStatus());
    }
}