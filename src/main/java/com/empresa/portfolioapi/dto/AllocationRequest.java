package com.empresa.portfolioapi.dto;

import jakarta.validation.constraints.NotNull;

public record AllocationRequest(

        @NotNull(message = "O identificador do membro é obrigatório.")
        Long memberId
) {
}