package com.splitmate.api.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RecentExpenseDto(
        UUID id,
        String description,
        String groupName,
        BigDecimal amount,
        String currency,
        String paidByDisplayName,
        Instant createdAt
) {}
