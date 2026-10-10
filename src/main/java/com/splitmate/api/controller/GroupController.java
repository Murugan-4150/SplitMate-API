package com.splitmate.api.controller;

import com.splitmate.api.dto.response.DashboardGroupDto;
import com.splitmate.api.dto.response.PageResponse;
import com.splitmate.api.entity.User;
import com.splitmate.api.service.GroupService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DashboardGroupDto>> getUserGroups(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        // Enforce maximum page size to prevent abuse
        int boundedSize = Math.min(Math.max(size, 1), 50);
        PageResponse<DashboardGroupDto> response = groupService.getUserGroups(
                currentUser.getId(),
                PageRequest.of(page, boundedSize)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<DashboardGroupDto> getGroupSummary(
            @AuthenticationPrincipal User currentUser,
            @PathVariable("id") UUID groupId) {
        DashboardGroupDto response = groupService.getGroupSummary(groupId, currentUser.getId());
        return ResponseEntity.ok(response);
    }
}
