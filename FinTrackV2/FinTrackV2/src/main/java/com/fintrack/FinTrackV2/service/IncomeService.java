package com.fintrack.FinTrackV2.service;

import com.fintrack.FinTrackV2.model.Income;
import com.fintrack.FinTrackV2.model.User;

import com.fintrack.FinTrackV2.repository.IncomeRepository;
import com.fintrack.FinTrackV2.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class IncomeService {

    private final IncomeRepository repository;

    private final UserRepository userRepository;

    public IncomeService(
            IncomeRepository repository,
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

    // ALL USER INCOME

    public List<Income> getAllIncome(){

        return repository.findByUser(
                getCurrentUser());
    }

    // SAVE INCOME

    public Income saveIncome(
            Income income){

        income.setUser(
                getCurrentUser());

        return repository.save(income);
    }

    // DELETE

    public void deleteIncome(Long id){

        repository.deleteById(id);
    }

    // TOTAL INCOME

    public Double getTotalIncome(){

        List<Income> incomes =
                repository.findByUser(
                        getCurrentUser());

        return incomes.stream()

                .mapToDouble(
                        Income::getAmount)

                .sum();
    }

    // GET BY ID
    public Income getIncomeById(Long id){
        return repository.findById(id).orElse(null);
    }
}