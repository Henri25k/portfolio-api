package com.empresa.portfolioapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectCreateRequest(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String name,

        @NotNull(message = "A data de início é obrigatória.")
        LocalDate startDate,

        @NotNull(message = "A previsão de término é obrigatória.")
        @FutureOrPresent(message = "A previsão de término deve ser hoje ou uma data futura.")
        LocalDate expectedEndDate,

        @NotNull(message = "O orçamento total é obrigatório.")
        @DecimalMin(value = "0.01", message = "O orçamento deve ser maior que zero.")
        BigDecimal totalBudget,

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
        String description,

        @NotNull(message = "O gerente responsável é obrigatório.")
        Long managerId
) {
}