package com.invest.controller;

import com.invest.model.Basket;
import com.invest.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/baskets")
public class BasketController {

    @Autowired
    private BasketService basketService;

    // Create a new basket by an Investment Advisor
    @PostMapping("/create")
    public ResponseEntity<String> createBasket(@RequestBody Basket basket, 
                                               @RequestHeader("role") String role,
                                               @RequestHeader("advisorEmail") String advisorEmail) {
        if (!"INVESTMENT_ADVISOR".equals(role)) {
            return ResponseEntity.status(403).body("Only Investment Advisors can create baskets.");
        }

        basketService.createBasket(advisorEmail, basket);
        return ResponseEntity.ok("Basket created successfully");
    }

    // Get all baskets created by a specific Investment Advisor
    @GetMapping("/advisor/{advisorEmail}")
    public ResponseEntity<List<Basket>> getBasketsByAdvisor(@PathVariable String advisorEmail) {
        return ResponseEntity.ok(basketService.getBasketsByAdvisor(advisorEmail));
    }

    // Get all available baskets
    @GetMapping("/all")
    public ResponseEntity<List<Basket>> getAllBaskets() {
        return ResponseEntity.ok(basketService.getAllBaskets());
    }
    
    
 // Mapping to return the difference between current_price and total_cost
    @GetMapping("/progressbasket/{basketId}")
    public ResponseEntity<String> getProgressOfBasket(@PathVariable Long basketId) {
        Basket basket = basketService.getBasketById(basketId);
        if (basket == null) {
            return ResponseEntity.status(404).body("Basket not found");
        }
        
        double progress = basket.getCurrentPrice() - basket.getTotalCost();
        
        progress = Math.round(progress * 10000.0) / 10000.0;
        
        double percent = Math.round((progress/basket.getTotalCost())*100 * 100.00)/ 100.00;
        
        return ResponseEntity.ok("Since the created time, \nthe Net return of Basket ID "+basketId +" named "+basket.getBasketName() +" : \n\n\n"+progress+" per basket \n\n"+"Percentage of net returns :"+percent+" %"+"\n\n\nwhich is based on the strategy\n"+basket.getStrategy());
    }
}
