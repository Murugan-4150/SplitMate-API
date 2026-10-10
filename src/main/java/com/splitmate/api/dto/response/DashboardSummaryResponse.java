package com.splitmate.api.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        long groupCount,
        BigDecimal totalYouOwe,
        BigDecimal totalOwedToYou,
        List<DashboardGroupDto> groups,
        List<RecentExpenseDto> recentExpenses,
        long unreadNotificationCount
) {}
