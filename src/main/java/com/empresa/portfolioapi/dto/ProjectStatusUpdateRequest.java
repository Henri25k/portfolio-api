package com.empresa.portfolioapi.dto;

import com.empresa.portfolioapi.enums.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record ProjectStatusUpdateRequest(

        @NotNull(message = "O novo status é obrigatório.")
        ProjectStatus status
) {
}