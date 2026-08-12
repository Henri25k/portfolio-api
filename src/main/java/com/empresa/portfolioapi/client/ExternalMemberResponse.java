package com.empresa.portfolioapi.client;

public record ExternalMemberResponse(
        Long id,
        String name,
        String role
) {
}