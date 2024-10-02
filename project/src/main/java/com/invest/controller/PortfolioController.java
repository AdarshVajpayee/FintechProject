package com.invest.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.invest.model.Portfolio;
import com.invest.model.Transaction;
import com.invest.service.PortfolioService;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    // Investor invests in a basket with a specified quantity
    @PostMapping("/invest")
    public ResponseEntity<String> investInBasket(@RequestParam String investorEmail, 
                                                 @RequestParam Long basketId, 
                                                 @RequestParam int quantity) {
        try {
            Portfolio portfolio = portfolioService.investInBasket(investorEmail, basketId, quantity);
            
            if (portfolio == null) {
                return ResponseEntity.badRequest().body("Investment failed: Insufficient wallet balance or invalid input.");
            }

            return ResponseEntity.ok("Investment successful");
        } catch (RuntimeException e) {
            // Handle specific exception message (e.g., insufficient funds)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    // Investor sells a basket with a specified quantity
    @PostMapping("/sell")
    public ResponseEntity<String> sellBasket(@RequestParam String investorEmail, 
                                             @RequestParam Long basketId, 
                                             @RequestParam int quantity) {
        portfolioService.sellBasket(investorEmail, basketId, quantity);
        return ResponseEntity.ok("Basket sold successfully");
    }

    // Get the portfolio of an investor
    @GetMapping("/investor/{investorEmail}")
    public ResponseEntity<?> getPortfolio(@PathVariable String investorEmail) {
        Portfolio portfolio = portfolioService.getPortfolio(investorEmail);
        
        // Handle null portfolio
        if (portfolio == null) {
            return ResponseEntity.status(404).body("Portfolio is empty or not found for the given investor.");
        }
        
        return ResponseEntity.ok(portfolio);
    }
    // Endpoint for the investor to get a summary of their investments
    @GetMapping("/summary")
    public ResponseEntity<List<Transaction>> getInvestmentSummary(@RequestParam String investorEmail) {
        List<Transaction> summary = portfolioService.getInvestmentSummary(investorEmail);
        return ResponseEntity.ok(summary);
    }

 // Endpoint to calculate portfolio return for the investor
    @GetMapping("/return")
    public ResponseEntity<String> calculatePortfolioReturn(@RequestHeader("investorEmail") String investorEmail) {
        double portfolioReturn = portfolioService.calculatePortfolioReturn(investorEmail);
        return ResponseEntity.ok("Portfolio Return: " + portfolioReturn);
    }

}
