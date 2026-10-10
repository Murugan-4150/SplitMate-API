package com.splitmate.api.dto.response;

import java.util.List;

public record SearchResultResponse(
        List<DashboardGroupDto> groups,
        List<RecentExpenseDto> expenses
) {}
