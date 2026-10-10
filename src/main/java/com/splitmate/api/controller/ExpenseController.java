package com.splitmate.api.controller;

import com.splitmate.api.dto.response.PageResponse;
import com.splitmate.api.dto.response.RecentExpenseDto;
import com.splitmate.api.entity.User;
import com.splitmate.api.service.ExpenseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/recent")
    public ResponseEntity<PageResponse<RecentExpenseDto>> getRecentExpenses(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        int boundedSize = Math.min(Math.max(size, 1), 50);
        PageResponse<RecentExpenseDto> response = expenseService.getRecentExpenses(
                currentUser.getId(),
                PageRequest.of(page, boundedSize)
        );
        return ResponseEntity.ok(response);
    }
}
