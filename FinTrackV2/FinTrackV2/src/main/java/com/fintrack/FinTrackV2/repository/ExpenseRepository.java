package com.fintrack.FinTrackV2.repository;

import com.fintrack.FinTrackV2.model.Expense;
import com.fintrack.FinTrackV2.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    // USER EXPENSES

    List<Expense> findByUser(
            User user);

    // SEARCH

    List<Expense>
    findByUserAndTitleContainingIgnoreCase(
            User user,
            String keyword);

    // RECENT

    List<Expense>
    findTop5ByUserOrderByIdDesc(
            User user);

    // CATEGORY ANALYTICS

    @Query("""

    SELECT e.category,
    SUM(e.amount)

    FROM Expense e

    WHERE e.user = :user

    GROUP BY e.category

    """)

    List<Object[]> getCategoryAnalytics(
            User user);

    // MONTHLY ANALYTICS

    @Query("""

    SELECT MONTH(e.date),
    SUM(e.amount)

    FROM Expense e

    WHERE e.user = :user

    GROUP BY MONTH(e.date)

    ORDER BY MONTH(e.date)

    """)

    List<Object[]> getMonthlyExpenses(
            User user);
}