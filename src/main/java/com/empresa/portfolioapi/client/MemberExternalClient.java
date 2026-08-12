package com.empresa.portfolioapi.client;

import com.empresa.portfolioapi.exception.BusinessException;
import com.empresa.portfolioapi.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class MemberExternalClient {


    @Value("${member-api.base-url}")
    private String baseUrl;

    public ExternalMemberResponse findById(Long memberId) {
        try {
            ExternalMemberResponse member = RestClient
                    .builder()
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri("/api/members/{id}", memberId)
                    .retrieve()
                    .body(ExternalMemberResponse.class);

            if (member == null) {
                throw new ResourceNotFoundException("Membro não encontrado na API externa.");
            }

            return member;

        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new ResourceNotFoundException("Membro não encontrado na API externa.");
            }

            throw new BusinessException("Não foi possível consultar a API externa de membros.");

        } catch (RestClientException exception) {
            throw new BusinessException(
                    "A API externa de membros não está disponível. Verifique se ela está em execução."
            );
        }
    }
}