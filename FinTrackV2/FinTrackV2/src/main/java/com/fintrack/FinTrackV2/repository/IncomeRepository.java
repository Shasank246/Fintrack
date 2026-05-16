package com.fintrack.FinTrackV2.repository;

import com.fintrack.FinTrackV2.model.Income;
import com.fintrack.FinTrackV2.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeRepository
        extends JpaRepository<Income, Long> {

    @Query("SELECT SUM(i.amount) FROM Income i")

    Double getTotalIncome();

    // USER-SPECIFIC METHODS

    List<Income> findByUser(
            User user);
}