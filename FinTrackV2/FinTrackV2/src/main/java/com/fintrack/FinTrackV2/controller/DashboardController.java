package com.fintrack.FinTrackV2.controller;

import com.fintrack.FinTrackV2.model.User;
import com.fintrack.FinTrackV2.service.ExpenseService;
import com.fintrack.FinTrackV2.service.IncomeService;
import com.fintrack.FinTrackV2.service.PdfService;
import com.fintrack.FinTrackV2.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final PdfService pdfService;
    private final UserService userService;

    public DashboardController(ExpenseService expenseService,
                               IncomeService incomeService,
                               PdfService pdfService,
                               UserService userService) {
        this.expenseService = expenseService;
        this.incomeService = incomeService;
        this.pdfService = pdfService;
        this.userService = userService;
    }

    private void addUserAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        model.addAttribute("userEmail", email);

        Optional<User> userOpt = userService.findByEmail(email);
        userOpt.ifPresent(u -> {
            model.addAttribute("username", u.getUsername());
            String pic = u.getProfilePic();
            model.addAttribute("profilePic",
                (pic != null && !pic.isBlank()) ? pic : null);
        });
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        addUserAttributes(model);

        double expense = expenseService.getTotalExpense();
        double income = incomeService.getTotalIncome();
        double balance = income - expense;

        model.addAttribute("income", income);
        model.addAttribute("expense", expense);
        model.addAttribute("balance", balance);
        model.addAttribute("recentExpenses", expenseService.getRecentExpenses());

        // Category pie chart
        List<Object[]> analytics = expenseService.getCategoryAnalytics();
        List<String> categories = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();
        for (Object[] row : analytics) {
            categories.add(row[0].toString());
            amounts.add(Double.parseDouble(row[1].toString()));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("amounts", amounts);

        // Monthly bar chart
        List<Object[]> monthlyData = expenseService.getMonthlyExpenses();
        String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        List<String> months = new ArrayList<>();
        List<Double> monthlyAmounts = new ArrayList<>();
        for (Object[] row : monthlyData) {
            int month = (Integer) row[0];
            months.add(monthNames[month - 1]);
            monthlyAmounts.add(Double.parseDouble(row[1].toString()));
        }
        model.addAttribute("months", months);
        model.addAttribute("monthlyAmounts", monthlyAmounts);

        return "dashboard";
    }

    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf() {
        Double income = incomeService.getTotalIncome();
        Double expense = expenseService.getTotalExpense();
        Double balance = income - expense;

        ByteArrayInputStream pdf = pdfService.generatePdf(
                expenseService.getAllExpenses(), income, expense, balance);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=fintrack-report.pdf");

        try {
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf.readAllBytes());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
