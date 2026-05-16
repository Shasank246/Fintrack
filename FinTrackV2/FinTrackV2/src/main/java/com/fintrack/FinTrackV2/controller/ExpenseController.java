package com.fintrack.FinTrackV2.controller;

import com.fintrack.FinTrackV2.model.Expense;

import com.fintrack.FinTrackV2.service.ExpenseService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/expenses")

public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(
            ExpenseService service){

        this.service = service;
    }

    @GetMapping

    public String expensePage(Model model){

        model.addAttribute(
                "expense",
                new Expense());

        model.addAttribute(
                "expenses",
                service.getAllExpenses());

        return "expense";
    }

    @PostMapping("/save")

    public String saveExpense(
            @ModelAttribute Expense expense){

        service.saveExpense(expense);

        return "redirect:/expenses";
    }

    @GetMapping("/delete/{id}")

    public String deleteExpense(
            @PathVariable Long id){

        service.deleteExpense(id);

        return "redirect:/expenses";
    }
    @GetMapping("/edit/{id}")

public String editExpense(
        @PathVariable Long id,
        Model model){

    Expense expense =
            service.getExpenseById(id);

    model.addAttribute(
            "expense",
            expense);

    return "edit-expense";
}
@PostMapping("/update")

public String updateExpense(
        @ModelAttribute Expense expense){

    service.saveExpense(expense);

    return "redirect:/expenses";
}
@GetMapping("/search")

public String searchExpenses(
        @RequestParam String keyword,
        Model model){

    model.addAttribute(
            "expenses",
            service.searchExpenses(keyword));

    model.addAttribute(
            "expense",
            new Expense());

    return "expense";
}
}