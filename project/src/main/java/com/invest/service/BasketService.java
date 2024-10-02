package com.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invest.model.Basket;
import com.invest.model.BasketStock;
import com.invest.model.Stock;
import com.invest.model.User;
import com.invest.repository.BasketRepository;
import com.invest.repository.StockRepository;
import com.invest.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BasketService {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UserRepository userRepository;

    public Basket createBasket(String advisorEmail, Basket basket) {
        // Find the advisor by email
        User advisor = userRepository.findByEmail(advisorEmail)
                .orElseThrow(() -> new RuntimeException("Advisor not found"));

        // Set the advisorId (assuming User entity has a getId() method)
        basket.setAdvisorId(advisor.getId());  // Correcting this line to get the ID of the advisor

        // Set created timestamp
        basket.setCreatedAt(LocalDateTime.now());

        double totalCost = 0.0;
        
        // Loop through stocks in the basket
        for (BasketStock basketStock : basket.getStocks()) {
            // Lookup stock by ticker symbol
            Stock stock = stockRepository.findByTickerSymbol(basketStock.getStock().getTickerSymbol())
                    .orElseThrow(() -> new RuntimeException("Stock not found: " + basketStock.getStock().getTickerSymbol()));

            // Set stock information in basketStock
            basketStock.setStock(stock);
            basketStock.setUnitPrice(stock.getClose());  // Set the unit price based on the stock's close price
            
            // Calculate total cost for the basket
            totalCost += stock.getClose() * basketStock.getQuantity();
        }

        // Set total cost and current price in the basket
        basket.setTotalCost(totalCost);
        basket.setCurrentPrice(totalCost);

        // Save and return the basket
        return basketRepository.save(basket);
    }


    public List<Basket> getBasketsByAdvisor(String advisorEmail) {
        User advisor = userRepository.findByEmail(advisorEmail)
                .orElseThrow(() -> new RuntimeException("Advisor not found"));

        // Use advisor.getId() since we are now querying by advisorId
        return basketRepository.findByAdvisorId(advisor.getId());
    }


    public List<Basket> getAllBaskets() {
        return basketRepository.findAll();
    }
    
 // Fetch a basket by its ID
    public Basket getBasketById(Long basketId) {
        return basketRepository.findById(basketId).orElse(null);
    }
}
