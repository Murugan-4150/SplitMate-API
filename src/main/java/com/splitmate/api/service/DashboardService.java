package com.splitmate.api.service;

import com.splitmate.api.dto.response.*;
import com.splitmate.api.entity.User;
import com.splitmate.api.repository.ExpenseRepository;
import com.splitmate.api.repository.ExpenseSplitRepository;
import com.splitmate.api.repository.GroupRepository;
import com.splitmate.api.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final NotificationRepository notificationRepository;
    private final GroupService groupService;
    private final ExpenseService expenseService;

    public DashboardService(GroupRepository groupRepository,
                            ExpenseRepository expenseRepository,
                            ExpenseSplitRepository expenseSplitRepository,
                            NotificationRepository notificationRepository,
                            GroupService groupService,
                            ExpenseService expenseService) {
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.notificationRepository = notificationRepository;
        this.groupService = groupService;
        this.expenseService = expenseService;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboardSummary(User currentUser) {
        UUID userId = currentUser.getId();

        long groupCount = groupRepository.countUserGroups(userId);
        BigDecimal totalYouOwe = expenseSplitRepository.calculateTotalYouOwe(userId);
        BigDecimal totalOwedToYou = expenseSplitRepository.calculateTotalOwedToYou(userId);

        List<DashboardGroupDto> groups = groupRepository.findUserGroups(userId, PageRequest.of(0, 4))
                .getContent()
                .stream()
                .map(groupService::toDashboardGroupDto)
                .toList();

        List<RecentExpenseDto> recentExpenses = expenseRepository.findRecentExpensesByUserGroups(userId, PageRequest.of(0, 5))
                .getContent()
                .stream()
                .map(expenseService::toRecentExpenseDto)
                .toList();

        long unreadNotifications = notificationRepository.countUnreadByUserId(userId);

        return new DashboardSummaryResponse(
                groupCount,
                totalYouOwe,
                totalOwedToYou,
                groups,
                recentExpenses,
                unreadNotifications
        );
    }

    @Transactional(readOnly = true)
    public BalanceSummaryResponse getBalanceSummary(User currentUser) {
        UUID userId = currentUser.getId();
        BigDecimal totalYouOwe = expenseSplitRepository.calculateTotalYouOwe(userId);
        BigDecimal totalOwedToYou = expenseSplitRepository.calculateTotalOwedToYou(userId);

        return new BalanceSummaryResponse(totalYouOwe, totalOwedToYou, "INR");
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getUnreadNotificationCount(User currentUser) {
        long count = notificationRepository.countUnreadByUserId(currentUser.getId());
        return new UnreadNotificationCountResponse(count);
    }

    @Transactional(readOnly = true)
    public SearchResultResponse search(User currentUser, String query, int page, int size) {
        if (query == null || query.trim().length() < 2) {
            throw new IllegalArgumentException("Search keyword must be at least 2 characters.");
        }

        UUID userId = currentUser.getId();
        Pageable pageable = PageRequest.of(page, size);

        List<DashboardGroupDto> groups = groupRepository.searchUserGroups(userId, query.trim(), pageable)
                .getContent()
                .stream()
                .map(groupService::toDashboardGroupDto)
                .toList();

        List<RecentExpenseDto> expenses = expenseRepository.searchUserExpenses(userId, query.trim(), pageable)
                .getContent()
                .stream()
                .map(expenseService::toRecentExpenseDto)
                .toList();

        return new SearchResultResponse(groups, expenses);
    }
}
