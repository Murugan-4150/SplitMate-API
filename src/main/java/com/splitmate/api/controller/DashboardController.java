package com.splitmate.api.controller;

import com.splitmate.api.dto.response.BalanceSummaryResponse;
import com.splitmate.api.dto.response.DashboardSummaryResponse;
import com.splitmate.api.dto.response.SearchResultResponse;
import com.splitmate.api.dto.response.UnreadNotificationCountResponse;
import com.splitmate.api.entity.User;
import com.splitmate.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @AuthenticationPrincipal User currentUser) {
        DashboardSummaryResponse response = dashboardService.getDashboardSummary(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balances/summary")
    public ResponseEntity<BalanceSummaryResponse> getBalanceSummary(
            @AuthenticationPrincipal User currentUser) {
        BalanceSummaryResponse response = dashboardService.getBalanceSummary(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<UnreadNotificationCountResponse> getUnreadNotificationCount(
            @AuthenticationPrincipal User currentUser) {
        UnreadNotificationCountResponse response = dashboardService.getUnreadNotificationCount(currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultResponse> search(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("q") String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        SearchResultResponse response = dashboardService.search(currentUser, query, page, size);
        return ResponseEntity.ok(response);
    }
}
