package com.invest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String basketName;
    private String description;

    @Column(length = 1000)  // Strategy should be able to handle up to 100 words (approximately 1000 characters)
    private String strategy;

    private LocalDateTime createdAt;
    //@JsonIgnore
    private Double totalCost;  // Original cost at the time of creation
    private Double currentPrice;  // Dynamically calculated based on current stock prices

    private Long advisorId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "basket_id")
    private List<BasketStock> stocks;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBasketName() {
        return basketName;
    }

    public void setBasketName(String basketName) {
        this.basketName = basketName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    // Dynamically calculate current price based on the latest stock prices
    public Double getCurrentPrice() {
        double updatedPrice = 0.0;
        for (BasketStock basketStock : stocks) {
            updatedPrice += basketStock.getLatestStockPrice() * basketStock.getQuantity();  // Get the latest stock price
        }
        return updatedPrice;
    }
    
    public Long getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(Long advisorId) {
        this.advisorId = advisorId;
    }

    public List<BasketStock> getStocks() {
        return stocks;
    }

    public void setStocks(List<BasketStock> stocks) {
        this.stocks = stocks;
    }

	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}
}
