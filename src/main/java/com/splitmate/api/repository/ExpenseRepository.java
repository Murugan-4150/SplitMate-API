package com.splitmate.api.repository;

import com.splitmate.api.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e WHERE e.group.id IN (SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId AND gm.active = true) ORDER BY e.createdAt DESC")
    Page<Expense> findRecentExpensesByUserGroups(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.group.id IN (SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId AND gm.active = true) AND LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY e.createdAt DESC")
    Page<Expense> searchUserExpenses(@Param("userId") UUID userId, @Param("query") String query, Pageable pageable);
}
