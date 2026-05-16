package com.fintrack.FinTrackV2.controller;

import com.fintrack.FinTrackV2.model.Income;

import com.fintrack.FinTrackV2.service.IncomeService;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/income")

public class IncomeController {

    private final IncomeService service;

    public IncomeController(
            IncomeService service){

        this.service = service;
    }

    @GetMapping

    public String incomePage(Model model){

        model.addAttribute(
                "income",
                new Income());

        model.addAttribute(
                "incomes",
                service.getAllIncome());

        return "income";
    }

    @PostMapping("/save")

    public String saveIncome(
            @ModelAttribute Income income){

        service.saveIncome(income);

        return "redirect:/income";
    }

    @GetMapping("/delete/{id}")

    public String deleteIncome(
            @PathVariable Long id){

        service.deleteIncome(id);

        return "redirect:/income";
    }

    @GetMapping("/edit/{id}")
    public String editIncome(@PathVariable Long id, Model model){
        Income income = service.getIncomeById(id);
        model.addAttribute("income", income);
        return "edit-income";
    }

    @PostMapping("/update")
    public String updateIncome(@ModelAttribute Income income){
        service.saveIncome(income);
        return "redirect:/income";
    }
}