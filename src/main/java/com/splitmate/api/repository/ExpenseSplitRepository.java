package com.splitmate.api.repository;

import com.splitmate.api.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, UUID> {

    @Query("SELECT COALESCE(SUM(es.owedAmount), 0) FROM ExpenseSplit es WHERE es.user.id = :userId AND es.expense.paidBy.id != :userId AND es.settled = false")
    BigDecimal calculateTotalYouOwe(@Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(es.owedAmount), 0) FROM ExpenseSplit es WHERE es.expense.paidBy.id = :userId AND es.user.id != :userId AND es.settled = false")
    BigDecimal calculateTotalOwedToYou(@Param("userId") UUID userId);
}
