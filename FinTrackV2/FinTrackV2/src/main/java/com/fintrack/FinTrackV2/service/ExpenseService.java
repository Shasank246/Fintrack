package com.fintrack.FinTrackV2.service;

import com.fintrack.FinTrackV2.model.Expense;
import com.fintrack.FinTrackV2.model.User;

import com.fintrack.FinTrackV2.repository.ExpenseRepository;
import com.fintrack.FinTrackV2.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service

public class ExpenseService {

    private final ExpenseRepository repository;

    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository repository,
            UserRepository userRepository){

        this.repository = repository;
        this.userRepository = userRepository;
    }

    // CURRENT USER

    private User getCurrentUser(){

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }

    // ALL USER EXPENSES

    public List<Expense> getAllExpenses(){

        return repository.findByUser(
                getCurrentUser());
    }

    // SAVE EXPENSE

    public Expense saveExpense(
            Expense expense){

        if(expense.getDate() == null){

            expense.setDate(
                    LocalDate.now());
        }

        expense.setUser(
                getCurrentUser());

        return repository.save(expense);
    }

    // DELETE

    public void deleteExpense(Long id){

        repository.deleteById(id);
    }

    // TOTAL EXPENSE

    public Double getTotalExpense(){

        List<Expense> expenses =
                repository.findByUser(
                        getCurrentUser());

        return expenses.stream()

                .mapToDouble(
                        Expense::getAmount)

                .sum();
    }

    // GET BY ID

    public Expense getExpenseById(Long id){

        return repository
                .findById(id)
                .orElse(null);
    }

    // RECENT EXPENSES

    public List<Expense> getRecentExpenses(){

        return repository
                .findTop5ByUserOrderByIdDesc(
                        getCurrentUser());
    }

    // SEARCH

    public List<Expense> searchExpenses(
            String keyword){

        return repository
                .findByUserAndTitleContainingIgnoreCase(
                        getCurrentUser(),
                        keyword);
    }

    // CATEGORY ANALYTICS

    public List<Object[]> getCategoryAnalytics(){

        return repository
                .getCategoryAnalytics(
                        getCurrentUser());
    }

    // MONTHLY ANALYTICS

    public List<Object[]> getMonthlyExpenses(){

        return repository
                .getMonthlyExpenses(
                        getCurrentUser());
    }
}