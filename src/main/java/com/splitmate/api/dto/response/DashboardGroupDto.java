package com.splitmate.api.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DashboardGroupDto(
        UUID id,
        String name,
        long activeMemberCount,
        Instant lastActivityAt,
        String imageUrl
) {}
