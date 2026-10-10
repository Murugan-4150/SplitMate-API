package com.splitmate.api.service;

import com.splitmate.api.dto.response.PageResponse;
import com.splitmate.api.dto.response.RecentExpenseDto;
import com.splitmate.api.entity.Expense;
import com.splitmate.api.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<RecentExpenseDto> getRecentExpenses(UUID userId, Pageable pageable) {
        Page<Expense> expensePage = expenseRepository.findRecentExpensesByUserGroups(userId, pageable);
        List<RecentExpenseDto> dtos = expensePage.getContent().stream()
                .map(this::toRecentExpenseDto)
                .toList();

        return new PageResponse<>(
                dtos,
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                expensePage.getNumber(),
                expensePage.getSize()
        );
    }

    public RecentExpenseDto toRecentExpenseDto(Expense expense) {
        String paidByName = expense.getPaidBy() != null ? expense.getPaidBy().getDisplayName() : "Unknown";
        String groupName = expense.getGroup() != null ? expense.getGroup().getName() : "Unknown";

        return new RecentExpenseDto(
                expense.getId(),
                expense.getDescription(),
                groupName,
                expense.getAmount(),
                expense.getCurrency(),
                paidByName,
                expense.getCreatedAt()
        );
    }
}
