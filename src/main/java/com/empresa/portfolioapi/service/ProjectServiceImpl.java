package com.empresa.portfolioapi.service;

import com.empresa.portfolioapi.client.ExternalMemberResponse;
import com.empresa.portfolioapi.client.MemberExternalClient;
import com.empresa.portfolioapi.dto.ProjectCreateRequest;
import com.empresa.portfolioapi.dto.ProjectResponse;
import com.empresa.portfolioapi.dto.ProjectStatusUpdateRequest;
import com.empresa.portfolioapi.dto.ProjectUpdateRequest;
import com.empresa.portfolioapi.entity.Member;
import com.empresa.portfolioapi.entity.Project;
import com.empresa.portfolioapi.enums.ProjectStatus;
import com.empresa.portfolioapi.exception.BusinessException;
import com.empresa.portfolioapi.exception.ResourceNotFoundException;
import com.empresa.portfolioapi.mapper.ProjectMapper;
import com.empresa.portfolioapi.repository.MemberRepository;
import com.empresa.portfolioapi.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMapper projectMapper;
    private final MemberExternalClient memberExternalClient;

    @Override
    public ProjectResponse create(ProjectCreateRequest request) {
        validateDates(request.startDate(), request.expectedEndDate());

        Member manager = findMember(request.managerId());

        Project project = new Project();
        project.setName(request.name());
        project.setStartDate(request.startDate());
        project.setExpectedEndDate(request.expectedEndDate());
        project.setTotalBudget(request.totalBudget());
        project.setDescription(request.description());
        project.setManager(manager);
        project.setStatus(ProjectStatus.EM_ANALISE);

        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ProjectResponse findById(Long id) {
        return projectMapper.toResponse(findProject(id));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Page<ProjectResponse> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::toResponse);
    }

    @Override
    public ProjectResponse update(Long id, ProjectUpdateRequest request) {
        validateDates(request.startDate(), request.expectedEndDate());

        Project project = findProject(id);
        Member manager = findMember(request.managerId());

        project.setName(request.name());
        project.setStartDate(request.startDate());
        project.setExpectedEndDate(request.expectedEndDate());
        project.setActualEndDate(request.actualEndDate());
        project.setTotalBudget(request.totalBudget());
        project.setDescription(request.description());
        project.setManager(manager);

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponse updateStatus(Long id, ProjectStatusUpdateRequest request) {
        Project project = findProject(id);
        ProjectStatus currentStatus = project.getStatus();
        ProjectStatus newStatus = request.status();

        validateStatusTransition(currentStatus, newStatus);

        project.setStatus(newStatus);

        if (newStatus == ProjectStatus.ENCERRADO && project.getActualEndDate() == null) {
            project.setActualEndDate(LocalDate.now());
        }

        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Override
    public void delete(Long id) {
        Project project = findProject(id);

        if (project.getStatus() == ProjectStatus.INICIADO
                || project.getStatus() == ProjectStatus.EM_ANDAMENTO
                || project.getStatus() == ProjectStatus.ENCERRADO) {
            throw new BusinessException(
                    "Projetos iniciados, em andamento ou encerrados não podem ser excluídos."
            );
        }

        projectRepository.delete(project);
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado."));
    }

    private Member findMember(Long id) {
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

    private void validateDates(LocalDate startDate, LocalDate expectedEndDate) {
        if (expectedEndDate.isBefore(startDate)) {
            throw new BusinessException(
                    "A previsão de término não pode ser anterior à data de início."
            );
        }
    }

    private void validateStatusTransition(ProjectStatus currentStatus, ProjectStatus newStatus) {
        if (newStatus == ProjectStatus.CANCELADO) {
            return;
        }

        if (currentStatus == ProjectStatus.CANCELADO || currentStatus == ProjectStatus.ENCERRADO) {
            throw new BusinessException("Não é possível alterar um projeto cancelado ou encerrado.");
        }

        if (getNextStatus(currentStatus) != newStatus) {
            throw new BusinessException(
                    "Transição de status inválida. Não é permitido pular etapas."
            );
        }
    }

    private ProjectStatus getNextStatus(ProjectStatus currentStatus) {
        return switch (currentStatus) {
            case EM_ANALISE -> ProjectStatus.ANALISE_REALIZADA;
            case ANALISE_REALIZADA -> ProjectStatus.ANALISE_APROVADA;
            case ANALISE_APROVADA -> ProjectStatus.INICIADO;
            case INICIADO -> ProjectStatus.PLANEJADO;
            case PLANEJADO -> ProjectStatus.EM_ANDAMENTO;
            case EM_ANDAMENTO -> ProjectStatus.ENCERRADO;
            case ENCERRADO, CANCELADO -> null;
        };
    }
}