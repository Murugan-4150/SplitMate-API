package com.splitmate.api.dto.response;

import java.math.BigDecimal;

public record BalanceSummaryResponse(
        BigDecimal totalYouOwe,
        BigDecimal totalOwedToYou,
        String currency
) {}
