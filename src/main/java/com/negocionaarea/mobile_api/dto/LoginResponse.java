package com.negocionaarea.mobile_api.dto;

import java.time.Instant;

public record LoginResponse(
        String token,
        String tipo,
        Instant expiresAt
) {
}

