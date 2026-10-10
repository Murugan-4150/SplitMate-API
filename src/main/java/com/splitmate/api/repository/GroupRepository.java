package com.splitmate.api.repository;

import com.splitmate.api.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.id = :userId AND gm.active = true ORDER BY gm.group.lastActivityAt DESC")
    Page<Group> findUserGroups(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.user.id = :userId AND gm.active = true")
    long countUserGroups(@Param("userId") UUID userId);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.id = :userId AND gm.active = true AND LOWER(gm.group.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY gm.group.lastActivityAt DESC")
    Page<Group> searchUserGroups(@Param("userId") UUID userId, @Param("query") String query, Pageable pageable);
}
