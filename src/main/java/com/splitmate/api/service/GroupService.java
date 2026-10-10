package com.splitmate.api.service;

import com.splitmate.api.dto.response.DashboardGroupDto;
import com.splitmate.api.dto.response.PageResponse;
import com.splitmate.api.entity.Group;
import com.splitmate.api.exception.ResourceNotFoundException;
import com.splitmate.api.repository.GroupMemberRepository;
import com.splitmate.api.repository.GroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<DashboardGroupDto> getUserGroups(UUID userId, Pageable pageable) {
        Page<Group> groupPage = groupRepository.findUserGroups(userId, pageable);
        List<DashboardGroupDto> dtos = groupPage.getContent().stream()
                .map(this::toDashboardGroupDto)
                .toList();

        return new PageResponse<>(
                dtos,
                groupPage.getTotalElements(),
                groupPage.getTotalPages(),
                groupPage.getNumber(),
                groupPage.getSize()
        );
    }

    @Transactional(readOnly = true)
    public DashboardGroupDto getGroupSummary(UUID groupId, UUID userId) {
        if (!groupMemberRepository.isUserMember(groupId, userId)) {
            throw new ResourceNotFoundException("Group not found or you are not a member of this group.");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found."));

        return toDashboardGroupDto(group);
    }

    public DashboardGroupDto toDashboardGroupDto(Group group) {
        long memberCount = groupMemberRepository.countActiveMembers(group.getId());
        return new DashboardGroupDto(
                group.getId(),
                group.getName(),
                memberCount,
                group.getLastActivityAt(),
                group.getImageUrl()
        );
    }
}
